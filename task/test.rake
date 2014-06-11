desc 'Runs the tests'
task :test => :compile do
  sh('cucumber features')
end
