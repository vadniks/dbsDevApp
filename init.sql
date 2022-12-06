use db;

select * from components;

create table components(
                           componentId integer unsigned not null auto_increment unique,
                           name varchar(255) not null unique,
                           type integer unsigned not null,
                           description varchar(255) not null,
                           cost integer unsigned not null,
                           image varchar(255) null,
                           count integer unsigned not null,
                           primary key (componentId)
);

create table clients(
                        clientId integer unsigned not null auto_increment unique,
                        name varchar(255) not null,
                        surname varchar(255) not null,
                        phone integer unsigned not null unique, /* +1 234 567 89 00 */
                        address varchar(255) not null,
                        email varchar(255) not null unique,
                        password varchar(255) not null,
                        primary key (clientId)
);

create table employeeInfo(
                             employeeId integer unsigned not null auto_increment unique,
                             name varchar(255) not null,
                             surname varchar(255) not null,
                             phone integer unsigned not null unique,
                             email varchar(255) not null unique,
                             salary integer unsigned not null,
                             jobType integer unsigned not null,
                             primary key (employeeId)
);

create table managers(
                         employeeId integer unsigned not null auto_increment unique,
                         foreign key (employeeId) references employeeInfo(employeeId) on update cascade on delete cascade,
                         primary key (employeeId)
);

create table deliveryWorkers(
                                employeeId integer unsigned not null auto_increment unique,
                                foreign key (employeeId) references employeeInfo(employeeId) on update cascade on delete cascade,
                                primary key (employeeId)
);

create table administrators(
                               employeeId integer unsigned not null auto_increment unique,
                               foreign key (employeeId) references employeeInfo(employeeId) on update cascade on delete cascade,
                               primary key (employeeId)
);

create table orders(
                       orderId integer unsigned not null auto_increment,
                       clientId integer unsigned not null,
                       managerId integer unsigned null,
                       deliveryWorkerId integer unsigned null,
                       cost integer unsigned not null,
                       count integer unsigned not null,
                       creationDatetime integer unsigned not null,
                       completionDatetime integer unsigned null,
                       foreign key (clientId) references clients(clientId) on update cascade on delete cascade,
                       foreign key (managerId) references managers(employeeId) on update cascade on delete set null,
                       foreign key (deliveryWorkerId) references deliveryWorkers(employeeId) on update cascade on delete set null,
                       primary key (orderId, clientId)
);

create table boughtComponents(
                                 componentId integer unsigned not null,
                                 orderId integer unsigned not null,
                                 clientId integer unsigned not null,
                                 foreign key (componentId) references components(componentId) on update cascade on delete cascade,
                                 foreign key (orderId) references orders(orderId) on update cascade on delete cascade,
                                 foreign key (clientId) references clients(clientId) on update cascade on delete cascade,
                                 primary key (componentId, orderId, clientId)
);

create unique index componentIdX on components(componentId);
create unique index componentNameX on components(name);
create index componentCostX on components(cost);
create unique index clientIdX on clients(clientId);
create unique index clientPhoneX on clients(phone);
create unique index clientEmailX on clients(email);
create index clientFullNameX on clients(name, surname);
create unique index employeeIdX on employeeInfo(employeeId);
create unique index employeePhoneX on employeeInfo(phone);
create unique index employeeEmailX on employeeInfo(email);
create index employeeFullNameX on employeeInfo(name, surname);
create index employeeJobTypeX on employeeInfo(jobType);
create unique index managerIdX on managers(employeeId);
create unique index deliveryWorkerIdX on deliveryWorkers(employeeId);
create unique index orderIdsX on orders(orderId, clientId);
create index orderIdX on orders(orderId);
create index orderClientIdX on orders(clientId);
create unique index boughtComponentIdsX on boughtComponents(componentId, orderId, clientId);

-- part 3, triggers ----------------------------------------------------------

create trigger deleteEmployeeInfoAfterManagerDeleted after delete on managers for each row begin
    delete from employeeInfo where employeeInfo.employeeId = OLD.employeeId;
    update orders set orders.managerId = null where orders.managerId = OLD.employeeId;
end;
create trigger deleteEmployeeInfoAfterDeliveryWorkerDeleted after delete on deliveryWorkers for each row begin
    delete from employeeInfo where employeeInfo.employeeId = OLD.employeeId;
    update orders set orders.deliveryWorkerId = null where orders.deliveryWorkerId = OLD.employeeId;
end;
create trigger deleteBoughtComponentsAfterOrderDeleted after delete on orders
    for each row delete from boughtComponents where boughtComponents.orderId = OLD.orderId;
create trigger deleteOrdersAfterClientDeleted after delete on clients
    for each row delete from orders where orders.clientId = OLD.clientId;
create trigger decreaseComponentCountOnBuyingIt after insert on boughtComponents
    for each row update components set count = count - 1 where components.componentId = NEW.componentId;
create trigger setJobTypeAfterManagerInserted after insert on managers
    for each row update employeeInfo set jobType = 0 where employeeId = NEW.employeeId;
create trigger setJobTypeAfterDeliveryWorkerInserted after insert on deliveryWorkers
    for each row update employeeInfo set jobType = 1 where employeeId = NEW.employeeId;

-- procedures, functions -----------------------------------------------------

delimiter $$
create procedure _select(which int(1)) begin case which
    when 0 then select * from components;
    when 1 then select * from clients;
    when 2 then select * from orders;
    when 3 then select * from employeeInfo;
    when 4 then select * from managers;
    when 5 then select * from deliveryWorkers;
    when 6 then select * from boughtComponents;
    end case; end$$
delimiter ;

delimiter $$
create procedure countOrders() begin select count(*) from orders; end$$
delimiter ;

delimiter $$
create function getEmployeeIdByEmail($email varchar(32)) returns int(6) reads sql data begin
    set @id = (select employeeId from employeeInfo where email = $email);
    return @id;
end$$
delimiter ;

delimiter $$
create procedure addManager(
    $name varchar(16),
    $surname varchar(16),
    $phone int(11),
    $email varchar(32),
    $salary int(4)
) begin
    insert into employeeInfo(name, surname, phone, email, salary, jobType)
    values($name, $surname, $phone, $email, $salary, 0);
    insert into managers(employeeId) values(getEmployeeIdByEmail($email));
end$$
delimiter ;

create procedure addDeliveryWorker(
    $name varchar(16),
    $surname varchar(16),
    $phone int(11),
    $email varchar(32),
    $salary int(4)
) begin
    insert into employeeInfo(name, surname, phone, email, salary, jobType)
    values($name, $surname, $phone, $email, $salary, 1);
    insert into deliveryWorkers(employeeId) values(getEmployeeIdByEmail($email));
end;
