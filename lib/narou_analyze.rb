require "thor"

require "narou_analyze/version"

module NarouAnalyze
  class CLI < Thor
    desc "version", "Display version"
    def version
      puts NarouAnalyze::VERSION
    end
  end
end

