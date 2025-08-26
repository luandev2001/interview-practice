## Cấu hình Database

### Create File Migration

bin/generate-migration.sh $name

### Run liquibase with docker and file liquibase.properties at folder resources

docker run --rm -v "$(pwd)/liquibase.properties":/liquibase/liquibase.properties \
-v "$(pwd)/db/changelog":/liquibase/db/changelog \
liquibase --defaultsFile=/liquibase/liquibase.properties update