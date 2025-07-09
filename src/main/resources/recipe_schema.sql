DROP TABLE IF EXITS ingredient;
DROP TABLE IF EXITS step;
DROP TABLE IF EXITS recipe_category;
DROP TABLE IF EXITS unit;
DROP TABLE IF EXITS category;
DROP TABLE IF EXITS recipe;

CREATE TABLE recipe(
recipe_id INT AUTO_INCREMENT NOT NULL,
recipe_name VARCHAR(126)NOT NULL,
notes TEXT,
num_serving INT,
prep_time TIME,
cook_time TIME,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMAY KEY(recipe_id)

) ;

CREATE TABLE category(
category_id INT AUTO INCREMENT NOT NULL,
category_name VARCHAR(64) NOT NULL,
PRIMAY ID (category_id)

);

CREATE TABLE unit(
unit_id INT AUTO INCREMENT NOT NULL,
unit_name_singular VARCHAR(32)NOT NULL,
unit_name_plural VARCHAR(34) NOT NULL,
PRIMAY ID (unit_id)
);

CREATE TABLE recipe_category(
recipe_id INT NOT NULL,
category_id INT NOT NULL,
FOREIGN KEY (recipe_id) REFERNCES recipe(recipe_id) ON DELETE CASCADE,
FOREGIN kEY(category_id) REFRENCES category(category_id) ON DELETE CASCADE
);


CREATE TABLE step(
step_id INT AUTO INCREMENT NOT NULL,
recipe_id INT NOT NULL,
step_order INT NOT NULL,
step_text TEXT NOT NULL,
PRIMARY KEY (step_id),
FOREIGN ID (recipe_id) REFRENCES recipe(recipe_id) ON DELETE CASCADE
);

CREATE TABLE ingredient(
ingredient_id INT AUTO INCREMENT NOT NULL,
recipe_id INT NOT NULL,
unit_id INT NOT NULL,
ingredient_name VARCHAR(64) NOT NULL,
instruction VARCHAR(64),
ingredient_order INT NULL,
amount DECIMAL(7,2),
PRIMARY ID (ingredient_id),
FOREIGN ID (recipe_id) REFRENCES recipe(recipe_id) ON DELETE CASCADE,
FOREIGN ID (unit_id) REFRENCES unit(unit_id) 

);


