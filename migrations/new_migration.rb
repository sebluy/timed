migration_name = ARGV[0]
time = Time.now.strftime('%Y%m%d%H%M%S')
migration = "#{time}-#{migration_name}"
up = "#{migration}.up.sql"
down = "#{migration}.down.sql"
File.open(up, 'w') {|file|}
File.open(down, 'w') {|file|}
