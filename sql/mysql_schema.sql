CREATE TABLE RATES (
  ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  BANK_NAME VARCHAR(50) NOT NULL,
  BANK_TIME TIMESTAMP NOT NULL,
  CATEGORY VARCHAR(30) NOT NULL,
  FROM_CURRENCY VARCHAR(10) NOT NULL,
  TO_CURRENCY VARCHAR(10) NOT NULL,
  BUY NUMERIC(10, 2),
  BUY_DIFF NUMERIC(10, 2),
  LONG_BUY_DIFF NUMERIC(10, 2),
  SELL NUMERIC(10, 2),
  SELL_DIFF NUMERIC(10, 2),
  LONG_SELL_DIFF NUMERIC(10, 2),
  CREATED TIMESTAMP NOT NULL
) ENGINE = 'InnoDB', DEFAULT CHARACTER SET = 'UTF8';
