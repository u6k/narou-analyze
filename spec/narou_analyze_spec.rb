RSpec.describe NarouAnalyze do
  it "has a version number" do
    expect(NarouAnalyze::VERSION).not_to be nil
  end
end

RSpec.describe NarouAnalyze::CLI do
  it "is version" do
    stdout = capture(:stdout) { NarouAnalyze::CLI.new.invoke("version") }

    expect(stdout).to eq "#{NarouAnalyze::VERSION}\n"
  end
end

