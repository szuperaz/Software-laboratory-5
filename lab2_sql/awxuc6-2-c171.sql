PROMPT 'Adatmanipuláció, 1';
INSERT INTO Persons (person_id, Name)
VALUES (seq_persons.NextVal, 'Zizi');PROMPT 'NEPTUN-kód: AWXUC6, Megoldott feladatok: Táblamódosító 1,2,3; Egyszerű 1,2,3,4,5,6,7; Bonyolult 1,2,3,4; Adatmanipuláció 1,2,3; Gondolkodtató 1';

-- inicializalo szkript
@/Users/szuperaz/Desktop/init.sql

-- sajat modositasok torlese
DROP TABLE vendegkonyv;

---------------------------------------------------------------------------------

-- tablamodosito feladatok
-- 1
PROMPT 'Táblamódosító feladatok, 1';
ALTER TABLE visits ADD create_date DATE DEFAULT CURRENT_DATE;

-- 2
PROMPT 'Táblamódosító feladatok, 2';
ALTER TABLE PLACES 
DROP CONSTRAINT CK_PLACES_PRICE;

ALTER TABLE PLACES
ADD CONSTRAINT CK_PLACES_PRICE CHECK 
(price In ('alacsony', 'magas', 'közepes', 'premium'))
ENABLE;

-- 3
PROMPT 'Táblamódosító feladatok, 3';
CREATE TABLE VENDEGKONYV 
(
  PLACE_ID NUMBER NOT NULL 
, PERSON_ID NUMBER    -- lehet nevtelen bejegyzes is
, SZOVEG NVARCHAR2(500) NOT NULL 
);

ALTER TABLE VENDEGKONYV
ADD CONSTRAINT VENDEGKONYV_FK1_PLACE_ID FOREIGN KEY
(
  PLACE_ID 
)
REFERENCES PLACES
(
  PLACE_ID 
)
ON DELETE CASCADE ENABLE;

ALTER TABLE VENDEGKONYV
ADD CONSTRAINT VENDEGKONYV_FK2_PERSON_ID FOREIGN KEY
(
  PERSON_ID 
)
REFERENCES PERSONS
(
  PERSON_ID 
)
ON DELETE CASCADE ENABLE;

----------------------------------------------------------------------------------

-- egyszeru feladatok
-- 1
PROMPT 'Egyszerű feladatok, 1';
SELECT * FROM places;

-- 2
PROMPT 'Egyszerű feladatok, 2';
SELECT name, address, phone FROM places WHERE name = 'E-Klub';

-- 3
PROMPT 'Egyszerű feladatok, 3';
SELECT name, address,
DECODE (price,
  'alacsony', '*',
  'közepes', '**',
  'magas', '***',
  'premium', '****')
FROM places;

-- 4
PROMPT 'Egyszerű feladatok, 4';
SELECT * FROM persons WHERE UPPER(name) LIKE '% IDA';

-- 5
PROMPT 'Egyszerű feladatok, 5';
SELECT places.name AS place_name, persons.name AS person_name
FROM visits, places, persons
WHERE visits.place_id = places.place_id
AND visits.person_id = persons.person_id;

-- 6
PROMPT 'Egyszerű feladatok, 6';
SELECT places.name AS place_name, persons.name AS person_name
FROM visits, places, persons
WHERE visits.place_id = places.place_id
AND visits.person_id = persons.person_id
AND places.price = 'alacsony';

-- 7
PROMPT 'Egyszerű feladatok, 7';
SELECT
	NVL2(places.name, places.name, 'SEHOL') AS place_name,
	persons.name AS person_name,
	persons.income
FROM persons, visits, places
WHERE persons.person_id = visits.person_id(+)
AND places.place_id(+) = visits.place_id
AND persons.income > 500000
ORDER BY place_name, person_name;

---------------------------------------------------------------------------------

-- kicsit bonyolultabb lekerdezesek
-- 1
PROMPT 'Kicsit bonyolultabb lekérdezések, 1';
-- akik nem jartak sehol, azokat kihagytam a listabol
SELECT 
  persons.name,
  ROUND(SUM(visits.frequency*visits.average_spending)/SUM(visits.frequency)) AS spending
FROM visits, persons
WHERE persons.person_id = visits.person_id
GROUP BY persons.name;

-- 2
PROMPT 'Kicsit bonyolultabb lekérdezések, 2';
SELECT
  places.name,
  ROUND(100*SUM(DECODE (visits.likes, 'I', 1, 'N', 0))/COUNT(*))||'%' AS likes
FROM places, visits
WHERE places.place_id = visits.place_id(+)
GROUP BY places.name
ORDER BY places.name;

-- 3
PROMPT 'Kicsit bonyolultabb lekérdezések, 3';
SELECT places.name
FROM places, visits
WHERE places.place_id = visits.place_id
GROUP BY places.name
HAVING AVG(frequency) > (SELECT AVG(frequency) FROM visits)
ORDER BY places.name;

-- 4
PROMPT 'Kicsit bonyolultabb lekérdezések, 4';
SELECT persons.name, SUM(visits.frequency*visits.average_spending*52)
FROM persons, visits
WHERE persons.person_id = visits.person_id
GROUP BY persons.name, persons.income
HAVING SUM(
  visits.frequency*
  visits.average_spending*
  (CASE WHEN visits.last_visit < TO_DATE('14-12-31', 'YY-MM-DD') THEN visits.last_visit
        ELSE TO_DATE('14-12-31', 'YY-MM-DD')
  END -
  CASE WHEN visits.first_visit > TO_DATE('14-01-01', 'YY-MM-DD') THEN visits.first_visit
       ELSE TO_DATE('14-01-01', 'YY-MM-DD')
  END)/7
  ) > persons.income*12*0.1;

---------------------------------------------------------------------------------

-- adatmanipulacio
-- 1
PROMPT 'Adatmanipuláció, 1';
INSERT INTO Persons (person_id, Name)
VALUES (seq_persons.NextVal, 'Zizi');

-- 2
PROMPT 'Adatmanipuláció, 2';
INSERT INTO vendegkonyv (place_id, szoveg)
  SELECT places.place_id, 'Senki nem volt meg itt'
  FROM places, visits
  WHERE places.place_id = visits.place_id (+)
  AND visits.place_id IS NULL;

-- 3
PROMPT 'Adatmanipuláció, 3';
UPDATE places SET name = 'Arató Club' WHERE name = 'Arató Disco';
DELETE from places WHERE name = 'Csili étterem';

---------------------------------------------------------------------------------

-- gondolkodtato feladat
-- 1, analitikus fv. nelkul
PROMPT 'Gondolkodtató feladat, 1 - 1. megoldás';
-- atallitottam a szamok megjeleniteset, hogy megfeleljen a feladat kovetelmenyeinek
ALTER SESSION SET nls_numeric_characters ='.,';
SELECT
  persons.name,
  -- a heti fogyasztast vettem alapul
  ROUND(100*visits.frequency*visits.average_spending/(sum_avg_spending.ammount), 2)||'%' AS out_of_total
FROM
  visits,
  persons,
  places,
  (SELECT SUM(visits.frequency*visits.average_spending) AS ammount
   FROM visits, places
   WHERE places.name = 'E-Klub'
   AND places.place_id = visits.place_id) sum_avg_spending
WHERE places.name = 'E-Klub'
AND places.place_id = visits.place_id
AND visits.person_id = persons.person_id;

-- 2, OVER-el
PROMPT 'Gondolkodtató feladat, 1 - 2. megoldás';
SELECT
  persons.name,
  ROUND(100*visits.frequency*visits.average_spending/(SUM(visits.frequency*visits.average_spending) OVER (PARTITION BY visits.place_id)), 2)||'%' AS out_of_total
FROM
  visits,
  persons,
  places
WHERE places.name = 'E-Klub'
AND places.place_id = visits.place_id
AND visits.person_id = persons.person_id;

