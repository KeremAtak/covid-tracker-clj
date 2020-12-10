CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--;;
CREATE TYPE math_operand AS ENUM('+','-','*','/');
--;;
CREATE TABLE calculations (
    id UUID DEFAULT uuid_generate_v4(),
    first_number INTEGER NOT NULL,
    last_number INTEGER NOT NULL,
    operand CHARACTER NOT NULL
);
