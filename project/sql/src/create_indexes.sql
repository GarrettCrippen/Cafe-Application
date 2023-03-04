CREATE INDEX index1
ON orders
USING BTREE
(timeStampRecieved);

CREATE INDEX index2
ON ItemStatus
USING BTREE
( orderid );