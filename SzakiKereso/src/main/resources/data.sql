-- data.sql
-- PROFESSIONAL tábla feltöltése 300 rekorddal
-- Feltételezzük, hogy a tábla neve PROFESSIONAL, az oszlopok:
-- id, name, specialty, phone, email, city, price_per_hour, urgent_available

-- 1. Városok lista (<30 000 fős megyei és kistelepülésekből)
-- Például:
-- Budapest, Debrecen, Szeged, Miskolc, Pécs, Győr, ...
-- (itt csak néhány, a valós listát bővítsd tovább)
CREATE ALIAS IF NOT EXISTS RAND INT FOR "java.lang.Math.random";
-- H2 nem engedi közvetlen CREATE ALIAS STRING, helyett véletlenség Java-ból ajánlott,
-- de egyszerűségként készítsük el a statikus INSERT-eket

-- 2. Generált szakemberek
-- Minta: érdemes egy scriptet írni, de itt bemutató
INSERT INTO PROFESSIONAL (name, specialty, phone, email, city, price_per_hour, urgent_available) VALUES
                                                                                                     ('Kiss Péter','Villanyszerelő','06' || FLOOR(RAND()*90+10) || FLOOR(RAND()*1e7),'kiss.peter@gmail.com','Szeged',4500,TRUE),
                                                                                                     ('Nagy Anna','Vízvezeték-szerelő','06' || FLOOR(RAND()*90+10) || FLOOR(RAND()*1e7),'anna.nagy@outlook.hu','Debrecen',3800,FALSE),
-- ... (ide ismételd meg 300 soron át, scriptelj külsőleg)
;

-- 3. Vélemények generálása
INSERT INTO REVIEW (rating, comment, professional_id) VALUES
                                                          (5,'Kiváló szakember, gyors és precíz munka.',1),
                                                          (2,'Időben eljött, de nem volt túl segítőkész.',1),
                                                          (4,'Ár-érték arány rendben van.',2),
                                                          (1,'Sokáig vártunk rá, rossz kommunikáció.',2)
-- ... (véletlenszerűen 2–5 bejegyzés a 300*0.7 szakemberhez)
;
