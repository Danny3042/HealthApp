"""
Add specified Swift files to Xcode project.pbxproj if missing:
- Ensures PBXFileReference exists for each file
- Ensures a PBXBuildFile exists and is in the PBXSourcesBuildPhase files list
- Ensures the fileRef is listed as a child of the iosApp group

Backup will be created before edits.

Run from repo root: python3 iosApp/add_files_pbxproj.py
"""
import re
import sys
from pathlib import Path
import uuid

proj_path = Path('iosApp/iosApp.xcodeproj/project.pbxproj')
if not proj_path.exists():
    print('project.pbxproj not found at', proj_path)
    sys.exit(1)

text = proj_path.read_text()
backup = proj_path.with_suffix('.pbxproj.add_files.bak')
backup.write_text(text)
print('Backup created at', backup)

# Files to ensure
FILES = [
    'ContentView.swift',
    'LoginView.swift',
    'HeroTabView.swift',
    'HabitTrackerView.swift',
    'ChatView.swift',
    'MeditationView.swift',
    'ProfileView.swift',
    'StressManagementView.swift',
    'AppSettings.swift'
]

# Helpers
HEX24 = lambda: uuid.uuid4().hex[:24].upper()

# Parse existing file references (id -> (name, path))
file_ref_re = re.compile(r"([0-9A-F]+) /\* ([^*]+) \*/ = \{[^}]*?name = ([^;]+);[^}]*?path = ([^;]+);", re.DOTALL)
# Alternative pattern if name not present
file_ref_re2 = re.compile(r"([0-9A-F]+) /\* ([^*]+) \*/ = \{[^}]*?path = ([^;]+);", re.DOTALL)

file_refs = {}  # name -> id
path_to_id = {}
for m in file_ref_re.finditer(text):
    fid = m.group(1)
    name = m.group(2).strip()
    # path group maybe quoted
    path = m.group(4).strip().strip('"')
    file_refs[name] = fid
    path_to_id[path] = fid
for m in file_ref_re2.finditer(text):
    fid = m.group(1)
    name = m.group(2).strip()
    path = m.group(3).strip().strip('"')
    if name not in file_refs:
        file_refs[name] = fid
    if path not in path_to_id:
        path_to_id[path] = fid

# Build file refs (PBXBuildFile) mapping fileRef -> buildFileId
build_file_re = re.compile(r"([0-9A-F]+) /\* ([^*]+) in Sources \*/ = \{isa = PBXBuildFile; fileRef = ([0-9A-F]+) /\* ([^*]+) \*/; \};")
build_files = {}
for m in build_file_re.finditer(text):
    bf_id = m.group(1)
    file_ref = m.group(3)
    build_files[file_ref] = bf_id

# Find Sources build phase block id and its files list
sources_phase_re = re.compile(r"([0-9A-F]+) /\* Sources \*/ = \{([^}]+)\};", re.DOTALL)
sources_phase = None
for m in sources_phase_re.finditer(text):
    sources_phase = m
    break

if sources_phase is None:
    print('Could not find PBXSourcesBuildPhase block for Sources. Exiting.')
    sys.exit(1)

sources_phase_id = sources_phase.group(1)
sources_phase_block = sources_phase.group(2)
# extract file ids inside files = ( ... )
files_list_re = re.compile(r"files = \((.*?)\);", re.DOTALL)
files_list_m = files_list_re.search(sources_phase_block)
existing_buildfile_ids = set()
if files_list_m:
    files_content = files_list_m.group(1)
    existing_buildfile_ids = set(re.findall(r"([0-9A-F]+) \/\* [^*]+ \*/", files_content))

# Find iosApp group block id
group_re = re.compile(r"([0-9A-F]+) /\* iosApp \*/ = \{([^}]+)\};", re.DOTALL)
group_m = None
for m in group_re.finditer(text):
    group_m = m
    break
if not group_m:
    print('Could not find iosApp PBXGroup entry; exiting')
    sys.exit(1)
group_id = group_m.group(1)
group_block = group_m.group(2)
# find children list
children_re = re.compile(r"children = \((.*?)\);", re.DOTALL)
children_m = children_re.search(group_block)
existing_children_ids = set()
if children_m:
    existing_children_ids = set(re.findall(r"([0-9A-F]+) \/\* [^*]+ \*/", children_m.group(1)))

# Helper to insert text before section end comments
def insert_before(section_end_comment, insert_text):
    idx = text.find(section_end_comment)
    if idx == -1:
        print('Could not find section end comment:', section_end_comment)
        return None
    return text[:idx] + insert_text + '\n' + text[idx:]

new_text = text
added_any = False
for fname in FILES:
    print('Processing', fname)
    fid = file_refs.get(fname) or path_to_id.get(fname)
    if fid:
        print('  fileRef exists:', fid)
    else:
        # create new fileRef
        fid = HEX = uuid.uuid4().hex[:24].upper()
        file_ref_entry = f"\t\t{HEX} /* {fname} */ = {{isa = PBXFileReference; lastKnownFileType = sourcecode.swift; path = {fname}; sourceTree = \"<group>\"; }};\n"
        # insert into PBXFileReference section before the end comment
        new_text = insert_before('/* End PBXFileReference section */', file_ref_entry)
        if new_text is None:
            print('  failed to insert fileRef for', fname)
            continue
        print('  inserted PBXFileReference', HEX)
        fid = HEX
        added_any = True
        # update maps
        file_refs[fname] = fid
        path_to_id[fname] = fid

    # ensure PBXBuildFile exists for this fileRef
    bf_id = build_files.get(fid)
    if bf_id:
        print('  buildFile exists:', bf_id)
    else:
        BF = uuid.uuid4().hex[:24].upper()
        build_file_entry = f"\t\t{BF} /* {fname} in Sources */ = {{isa = PBXBuildFile; fileRef = {fid} /* {fname} */; }};\n"
        new_text = insert_before('/* End PBXBuildFile section */', build_file_entry)
        if new_text is None:
            print('  failed to insert PBXBuildFile for', fname)
            continue
        print('  inserted PBXBuildFile', BF)
        added_any = True
        build_files[fid] = BF
        bf_id = BF

    # ensure bf_id is referenced in sources phase
    if bf_id in existing_buildfile_ids:
        print('  already in Sources phase')
    else:
        # insert build file id into files list of sources phase
        # find files = ( ... ) in the sources_phase block index
        # operate on new_text
        sp_match = sources_phase_re.search(new_text)
        if not sp_match:
            print('  could not find sources phase after edits')
            continue
        sp_block = sp_match.group(0)
        # insert before the closing ');' of files = (
        sp_files_m = files_list_re.search(sp_block)
        if sp_files_m:
            files_block = sp_files_m.group(1)
            insertion_point = sp_match.start() + sp_files_m.start(1) + len(files_block)
            insert_str = f"\n\t\t\t{bf_id} /* {fname} in Sources */,"
            new_text = new_text[:insertion_point] + insert_str + new_text[insertion_point:]
            existing_buildfile_ids.add(bf_id)
            print('  added buildFile to Sources phase:', bf_id)
            added_any = True
        else:
            print('  could not find files list inside sources phase')

    # ensure fileRef is a child of iosApp group
    if fid in existing_children_ids:
        print('  already a child of iosApp group')
    else:
        # insert into the children list of the group
        gm = group_re.search(new_text)
        if not gm:
            print('  could not find iosApp group after edits')
            continue
        group_block_start = gm.start(2)
        g_children_m = children_re.search(gm.group(2))
        if g_children_m:
            # compute insertion point relative to full text
            abs_insertion = gm.start(2) + g_children_m.start(1) + len(g_children_m.group(1))
            insert_str = f"\n\t\t\t{fid} /* {fname} */,"
            new_text = new_text[:abs_insertion] + insert_str + new_text[abs_insertion:]
            existing_children_ids.add(fid)
            print('  added fileRef to iosApp group:', fid)
            added_any = True
        else:
            print('  could not find children list for iosApp group')

# Save if changed
if added_any:
    proj_path.write_text(new_text)
    print('project.pbxproj updated')
else:
    print('No changes required')

print('Done')
