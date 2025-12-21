#!/usr/bin/env ruby
# Script to add Swift source files to an Xcode project target using the xcodeproj gem.
# Usage:
#   gem install xcodeproj
#   ruby add_files_to_xcode_target.rb /path/to/Your.xcodeproj TargetName file1.swift file2.swift ...

require 'xcodeproj'

if ARGV.length < 3
  puts "Usage: ruby add_files_to_xcode_target.rb <xcodeproj_path> <target_name> <file1> [file2 ...]"
  exit 1
end

xcodeproj_path = ARGV.shift
target_name = ARGV.shift
files = ARGV

project = Xcodeproj::Project.open(xcodeproj_path)

target = project.targets.find { |t| t.name == target_name }
if target.nil?
  puts "Target '#{target_name}' not found in project #{xcodeproj_path}. Available targets:"
  project.targets.each { |t| puts " - #{t.name}" }
  exit 1
end

# Find or create the group 'AddedByScript' to put files in
group = project.main_group.find_subpath('AddedByScript', true)
project.main_group << group unless project.main_group.children.include?(group)

files.each do |file_relative|
  absolute = File.expand_path(file_relative, Dir.pwd)
  unless File.exist?(absolute)
    puts "Warning: file not found: #{absolute} (skipping)"
    next
  end

  # Add file reference to the group
  file_ref = group.files.find { |f| f.path == absolute } || group.new_reference(absolute)

  # Add file to target sources if not already present
  unless target.source_build_phase.files_references.include?(file_ref)
    target.add_file_references([file_ref])
    puts "Added #{file_relative} to target #{target_name}"
  else
    puts "Already present: #{file_relative} in target #{target_name}"
  end
end

project.save
puts "Done. Remember to open Xcode and verify the files are in the correct group and target."
