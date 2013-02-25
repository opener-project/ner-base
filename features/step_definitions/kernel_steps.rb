Given /^the fixture file "(.*?)"$/ do |filename|
  @input = fixture_file(filename)
  @filename = filename
end

Given /^I put it through the kernel$/ do
  @tmp_filename = "output_#{rand(1000)}_#{@filename}"
  @output = tmp_file(@tmp_filename)
  
  `cat #{@input} | java -jar #{KERNEL_CORE} -stdin > #{@output}`
end

Then /^the output should match the fixture "(.*?)"$/ do |filename|
  fixture_output = File.read(fixture_file(filename))
  output = File.read(@output)
  output.should eql(fixture_output)
end

def fixture_file(filename)
  File.expand_path("../../../features/fixtures/#{filename}", __FILE__)
end

def tmp_file(filename)
  File.expand_path("../../../tmp/#{filename}", __FILE__)
end
