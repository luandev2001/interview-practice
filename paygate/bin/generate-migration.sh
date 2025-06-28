#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 migration-name"
  exit 1
fi

timestamp=$(date +%Y%m%d%H%M%S)
filename="src/main/resources/db/changelog/version/${timestamp}-$1.xml"

inner_content="        <!-- Add changes here -->"
if [[ $1 == create_* ]]; then
  table_name=$(echo "$1" | sed 's/^create_//')
  inner_content=$(cat <<EOF
        <createTable tableName="$table_name">
            <column name="id" type="uuid" defaultValueComputed="gen_random_uuid()">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="created_at" type="timestamp" defaultValueComputed="now()"/>
            <column name="updated_at" type="timestamp" defaultValueComputed="now()"/>
            <!-- Add changes here -->
        </createTable>
EOF
)
fi

cat <<EOF > $filename
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

    <changeSet id="$timestamp-$1" author="xuanluan">
$inner_content
    </changeSet>

</databaseChangeLog>
EOF

echo "Created: $filename"
