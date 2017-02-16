require 'json'

module Bibliothecary
  module Parsers
    class Hex
      include Bibliothecary::Analyser

      def self.mapping
        {
          /^mix\.exs$/ => :parse_mix,
          /^mix\.lock$/ => :parse_mix_lock
        }
      end

      def self.parse_mix(manifest)
        response = Typhoeus.post("https://mix-deps-json.herokuapp.com/", body: manifest)
        json = JSON.parse response.body

        json.map do |name, version|
          {
            name: name,
            version: version,
            type: "runtime"
          }
        end
      rescue
        []
      end

      def self.parse_mix_lock(manifest)
        response = Typhoeus.post("https://mix-deps-json.herokuapp.com/lock", body: manifest)
        json = JSON.parse response.body

        json.map do |name, info|
          {
            name: name,
            version: info['version'],
            type: "runtime"
          }
        end
      rescue
        []
      end
    end
  end
end
