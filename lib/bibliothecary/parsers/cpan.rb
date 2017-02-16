require 'yaml'
require 'json'

module Bibliothecary
  module Parsers
    class CPAN
      include Bibliothecary::Analyser

      def self.mapping
        {
          /^META\.json$/i => :parse_json_manifest,
          /^META\.yml$/i => :parse_yaml_manifest
        }
      end

      def self.parse_json_manifest(file_contents)
        manifest = JSON.parse file_contents
        manifest['prereqs'].map do |group, deps|
          map_dependencies(deps, 'requires', 'runtime')
        end.flatten
      end

      def self.parse_yaml_manifest(file_contents)
        manifest = YAML.load file_contents
        map_dependencies(manifest, 'requires', 'runtime')
      end

      def self.map_dependencies(hash, key, type)
        hash.fetch(key,[]).map do |name, requirement|
          {
            name: name,
            requirement: requirement,
            type: type
          }
        end
      end
    end
  end
end
