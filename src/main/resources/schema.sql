create table if not exists users (
    user_id bigint generated always as identity not null,
    user_name varchar not null,
    user_email varchar not null,
    constraint pk_user primary key (user_id),
    constraint user_email_unique unique (user_email)
);

create table if not exists request (
    request_id bigint generated always as identity not null,
    description varchar not null,
    owner_id bigint not null,
    created timestamp without time zone not null,
    constraint pk_request primary key (request_id),
    constraint fk_request_to_users foreign key (owner_id) references users (user_id) on delete cascade
);

create table if not exists item (
    item_id bigint generated always as identity not null,
    item_name varchar(50) not null,
    description varchar(200),
    owner_id bigint not null,
    available boolean not null,
    request_id bigint,
    constraint pk_item primary key (item_id),
    constraint fk_item_to_users foreign key (owner_id) references users (user_id) on delete cascade,
    constraint fk_item_to_request foreign key (request_id) references request (request_id) on delete set null
);

create table if not exists booking (
    booking_id bigint generated always as identity not null,
    booker_id bigint not null,
    item_id bigint not null,
    state varchar(50) not null,
    start_date_time timestamp without time zone not null,
    end_date_time timestamp without time zone not null,
    constraint pk_booking primary key (booking_id),
    constraint fk_booking_to_users foreign key (booker_id) references users (user_id) on delete cascade,
    constraint fk_booking_to_item foreign key (item_id) references item (item_id) on delete cascade
);

create table if not exists comment (
    comment_id bigint generated always as identity not null,
    comment_text varchar(2000) not null,
    item_id bigint not null,
    author_id bigint not null,
    created timestamp without time zone not null,
    constraint pk_comment primary key (comment_id),
    constraint fk_comment_to_item foreign key (item_id) references item (item_id) on delete cascade,
    constraint fk_comment_to_users foreign key (author_id) references users (user_id) on delete cascade
);
