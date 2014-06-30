Given /^the fixture file "(.*?)"$/ do |filename|
  @input = fixture_file(filename)
end

Given /^I put them through the kernel$/ do
  @output, *_ = kernel.run(File.read(@input))
end

Then /^the output should match the fixture "(.*?)"$/ do |filename|
  expected = File.read(fixture_file(filename))

  @output.should eql(expected)
end

def fixture_file(filename)
  return File.expand_path("../../fixtures/#{filename}", __FILE__)
end
