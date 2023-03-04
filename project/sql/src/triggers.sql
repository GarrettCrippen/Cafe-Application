DROP TRIGGER IF EXISTS name ON orders;
DROP SEQUENCE IF EXISTS orders_orderid_seq;
CREATE SEQUENCE orders_orderid_seq START WITH 86654;

CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION func_name()
RETURNS "trigger" AS
$BODY$
BEGIN
NEW.orderId := nextval('orders_orderid_seq');
NEW.timestamprecieved := Now();
NEW.total := 0;
RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;


CREATE TRIGGER name BEFORE INSERT
    ON orders FOR EACH ROW
    EXECUTE PROCEDURE func_name();