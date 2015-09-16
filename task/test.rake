desc 'Runs the tests'
task :test => [:compile, :cucumber, :rspec]
