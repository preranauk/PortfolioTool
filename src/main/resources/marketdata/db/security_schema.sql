
CREATE TABLE securities (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            ticker VARCHAR(50) NOT NULL,
                            identifier VARCHAR(50) NOT NULL UNIQUE,
                            type ENUM ('Stock', 'Call', 'Put') NOT NULL,
                            maturity_date DATE,
                            strike_price DECIMAL(10,2),
                            current_price DECIMAL(10,2),
                            stock_volatility DECIMAL(3,2), CHECK (stock_volatility BETWEEN 0 AND 1),
                            stock_return DECIMAL(3,2) CHECK (stock_return BETWEEN 0 AND 1)
);