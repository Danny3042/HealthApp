#!/usr/bin/env python3
"""
Simple fallback script to fix duplicated 'iosApp/iosApp/' path prefixes in project.pbxproj.
It makes a backup copy before modifying.
Run from the repository root: python3 iosApp/fix_pbxproj_paths.py
"""
import sys
from pathlib import Path

proj_path = Path('iosApp/iosApp.xcodeproj/project.pbxproj')
if not proj_path.exists():
    print(f'Error: project file not found at {proj_path}', file=sys.stderr)
    sys.exit(1)

text = proj_path.read_text()
backup = proj_path.with_suffix('.pbxproj.bak')
backup.write_text(text)
print(f'Backup written to {backup}')

# Replace occurrences of path = iosApp/iosApp/<file> with path = <file>
new_text = text.replace('path = iosApp/iosApp/', 'path = ')
# Additionally, remove any accidental doubled "iosApp/" in group path definitions
new_text = new_text.replace('path = iosApp/iosApp', 'path = iosApp')

if new_text == text:
    print('No duplicated prefixes found; nothing changed.')
else:
    proj_path.write_text(new_text)
    print('Fixed duplicated iosApp/iosApp/ prefixes in project.pbxproj')

# Print count of occurrences for verification
count = new_text.count('path = iosApp/iosApp/')
print(f"Remaining 'path = iosApp/iosApp/' occurrences: {count}")

print('Done.')

