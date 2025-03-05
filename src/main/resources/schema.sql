create table if not exists taco_order
(
    id              int auto_increment primary key,
    delivery_name   varchar(50) not null,
    delivery_street varchar(50) not null,
    delivery_city   varchar(50) not null,
    delivery_state  varchar(2)  not null,
    delivery_zip    varchar(10) not null,
    cc_number       varchar(16) not null,
    cc_expiration   varchar(5)  not null,
    cc_cvv          varchar(3)  not null,
    placed_at       timestamp   not null
);

create table if not exists taco
(
    id            int auto_increment primary key,
    name          varchar(50) not null,
    taco_order_id int         not null,
    created_at    timestamp   not null,
    foreign key (taco_order_id) references taco_order (id) on delete cascade
);


create table if not exists ingredient
(
    id   varchar(4) primary key,
    name varchar(25) not null,
    type varchar(10) not null
);

create table if not exists ingredient_ref
(
    ingredient_id varchar(4) not null,
    taco_id       int        not null,
    primary key (ingredient_id, taco_id),
    foreign key (ingredient_id) references ingredient (id) on delete cascade,
    foreign key (taco_id) references taco (id) on delete cascade
);