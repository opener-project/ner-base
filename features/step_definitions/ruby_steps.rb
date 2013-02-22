Before do
  @ruby_version = nil
end

#After do
#end

When /^I check the Ruby version number$/ do
  @ruby_version = `ruby -v` rescue 'Ruby not installed'
end

Then /^the result should be "([^\"]*)"$/ do |version|
  @ruby_version.should include("ruby #{version}")
end