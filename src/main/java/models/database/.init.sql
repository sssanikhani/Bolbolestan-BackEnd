create database if not exists bolbolestan;
use bolbolestan;

create table if not exists Course(
	code 	char(10),
    name 	char(100),
    type 	char(100),
    units 	integer,
    primary key(code)
);

create table if not exists Prerequisites(
	code 		char(10),
	precode 	char(10),
    primary key(code, precode),
    foreign key(code) references Course(code),
    foreign key(precode) references Course(code)
);

create table if not exists Offering(
	class_code		char(10),
    course_code		char(10),
    instructor		char(100),
    capacity		integer,
    primary key(course_code, class_code),
    foreign key(course_code) references Course(code) on delete cascade
);

create table if not exists OfferingClassTime(
	course_code char(10),
    class_code  char(10),
    time	    char(15),
    primary key(course_code, class_code),
    foreign key(course_code, class_code) references Offering(course_code, class_code) on delete cascade
);

create table if not exists OfferingDays(
	course_code char(10),
    class_code  char(10),
    day			char(10),
    primary key(course_code, class_code, day),
    foreign key(course_code, class_code) references OfferingClassTime(course_code, class_code) on delete cascade
);

create table if not exists OfferingExamTime(
    course_code char(10),
    class_code  char(10),
    start		char(50),
    end			char(50),
    primary key(course_code, class_code),
    foreign key(course_code, class_code) references Offering(course_code, class_code) on delete cascade
);

create table if not exists Student(
	id				char(15),
	name			char(100),
    second_name 	char(100),
    email           char(50),
    password        char(50),
    birth_date		char(15),
    field			char(100),
    faculty			char(100),
    level			char(100),
    status			char(100),
	image			char(200),
    primary key(id)
);

create table if not exists Term(
	student_id		char(15),
    term			int,
    primary key(student_id, term),
    foreign key(student_id) references Student(id) on delete cascade
);

create table if not exists Grade(
	student_id		char(15),
    course_code		char(10),
    term			int,
    grade			float,
    primary key(student_id, term, course_code),
    foreign key(student_id, term) references Term(student_id, term) on delete no action,
    foreign key(course_code) references Course(code) on delete cascade
);

create table if not exists StudentChosenOfferings(
	student_id		char(15),
	course_code		char(10),
    class_code		char(10),
    primary key (student_id, course_code, class_code),
    foreign key (student_id) references Student(id) on delete cascade,
    foreign key (course_code, class_code) references Offering(course_code, class_code) on delete cascade
);


create table if not exists StudentLastPlan(
	student_id		char(15),
    course_code		char(10),
    class_code 		char(10),
    primary key (student_id, course_code, class_code),
    foreign key (student_id) references Student(id) on delete cascade,
    foreign key (course_code, class_code) references Offering(course_code, class_code) on delete cascade
);

create table if not exists RegisteredStudents(
    course_code     char(10),
    class_code      char(10),
    student_id      char(15),
    primary key (course_code, class_code, student_id),
    foreign key (course_code, class_code) references Offering(course_code, class_code) on delete cascade,
    foreign key (student_id) references Student(id) on delete cascade
);

create table if not exists WaitingStudents(
    course_code     char(10),
    class_code      char(10),
    student_id      char(15),
    insert_time     datetime,
    primary key (course_code, class_code, student_id),
    foreign key (course_code, class_code) references Offering(course_code, class_code) on delete cascade,
    foreign key (student_id) references Student(id) on delete cascade
);
