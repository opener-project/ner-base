namespace :java do
  desc 'Builds the Java core'
  task :compile do
    Dir.chdir('core') do
      sh('mvn package')
    end
  end

  desc 'Cleans up Java files'
  task :clean do
    Dir.chdir('core') do
      sh('mvn clean')
    end
  end
end
