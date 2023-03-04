REM #!/bin/bash
REM DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
REM psql -h localhost -p $PGPORT $USER"_DB" < $DIR/../src/create_tables.sql
REM psql -h localhost -p $PGPORT $USER"_DB" < $DIR/../src/create_indexes.sql
REM psql -h localhost -p $PGPORT $USER"_DB" < $DIR/../src/load_data.sql

"C:\Program Files (x86)\PostgreSQL\9.3\bin\psql.exe" -f "C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\sql\src\create_tables.sql"  postgresql://postgres:123@localhost:5432/postgres
"C:\Program Files (x86)\PostgreSQL\9.3\bin\psql.exe" -f "C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\sql\src\create_indexes.sql"  postgresql://postgres:123@localhost:5432/postgres
"C:\Program Files (x86)\PostgreSQL\9.3\bin\psql.exe" -f "C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\sql\src\load_data.sql"  postgresql://postgres:123@localhost:5432/postgres
"C:\Program Files (x86)\PostgreSQL\9.3\bin\psql.exe" -f "C:\Users\playt\Desktop\Spring 2022\CS166\Projects\phase3\project-1\project\sql\src\triggers.sql"  postgresql://postgres:123@localhost:5432/postgres
