-- Seed data: five hotels, their room types and rooms, a few hundred guests, and six months of
-- reservations around today, so the search, the guest pages and the front desk all have something
-- real to show. Dates are relative to current_date and the generator is seeded, so every fresh
-- database looks the same relative to the day it was created. Staff sign in as desk / kestrel-desk.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO hotel (slug, name, city, country, time_zone, stars, tagline, description, hero_image, gallery, amenities,
                   check_in_time, check_out_time, currency, tax_bp) VALUES
('harborview', 'Kestrel & Vine Harborview', 'Port Ellery', 'Aurelian Coast', 'Europe/Lisbon', 5,
 'Harbour light and slow mornings',
 'A former customs house on the quay, rebuilt around a sea-water pool. Every room looks over the fishing harbour, breakfast runs until noon, and the terrace bar pours the coast''s white wines at sunset.',
 '/images/hotels/harborview/hero.webp',
 ARRAY['/images/hotels/harborview/pool.webp', '/images/hotels/harborview/room.webp'],
 ARRAY['Sea-water pool', 'Spa & hammam', 'Harbour terrace bar', 'Breakfast until noon', 'Fitness studio', 'Valet parking', 'Free Wi-Fi'],
 '15:00', '11:00', 'EUR', 1200),
('alder-quarter', 'Kestrel & Vine Alder Quarter', 'Marrowgate', 'Northern Reach', 'Europe/London', 4,
 'Old-town rooms above a bookbinder''s lane',
 'Three merchant houses joined at the hip in the oldest quarter of Marrowgate. Creaking oak stairs, quiet modern rooms, and a library bar that stays open as long as you keep reading.',
 '/images/hotels/alder-quarter/hero.webp',
 ARRAY['/images/hotels/alder-quarter/library.webp', '/images/hotels/alder-quarter/room.webp'],
 ARRAY['Library bar', 'Courtyard garden', 'Bike hire', 'Pet friendly', 'Free Wi-Fi'],
 '15:00', '11:00', 'GBP', 2000),
('saltmeadow', 'Kestrel & Vine Saltmeadow Lodge', 'Saltmeadow', 'Aurelian Coast', 'Europe/Lisbon', 4,
 'Vineyard cottages between the dunes and the vines',
 'Low stone cottages scattered through a working vineyard a mile from the dunes. Cellar tastings every evening, a farm kitchen that cooks what the garden gave it that morning, and nothing to do in between.',
 '/images/hotels/saltmeadow/hero.webp',
 ARRAY['/images/hotels/saltmeadow/vines.webp', '/images/hotels/saltmeadow/room.webp'],
 ARRAY['Cellar tastings', 'Farm kitchen', 'Outdoor pool', 'Bikes & trails', 'EV charging', 'Free Wi-Fi'],
 '16:00', '11:00', 'EUR', 900),
('summit-house', 'Kestrel & Vine Summit House', 'Ardent Peak', 'High Vale', 'Europe/Zurich', 5,
 'Ski-in mornings, fireside evenings',
 'A timber lodge at the top of the Ardent gondola. Ski storage with boot warmers, a heated outdoor pool facing the glacier, and a fireside lounge that does fondue for two after the lifts close.',
 '/images/hotels/summit-house/hero.webp',
 ARRAY['/images/hotels/summit-house/pool.webp', '/images/hotels/summit-house/room.webp'],
 ARRAY['Ski-in / ski-out', 'Heated outdoor pool', 'Sauna', 'Fireside lounge', 'Ski storage', 'Free Wi-Fi'],
 '16:00', '10:00', 'EUR', 800),
('lantern-row', 'Kestrel & Vine Lantern Row', 'Calloway', 'Eastern Seaboard', 'America/New_York', 4,
 'A design hotel in the old lantern works',
 'Brick, steel and tall factory windows in the building that once made the city''s street lamps. A rooftop with skyline views, a lobby café that turns into a cocktail bar, and rooms built for working and sleeping well.',
 '/images/hotels/lantern-row/hero.webp',
 ARRAY['/images/hotels/lantern-row/rooftop.webp', '/images/hotels/lantern-row/room.webp'],
 ARRAY['Rooftop bar', 'Lobby café', 'Co-working lounge', '24-hour gym', 'Free Wi-Fi'],
 '15:00', '12:00', 'USD', 1475);

INSERT INTO room_type (hotel_id, code, name, description, max_guests, beds, size_sqm, image, base_rate_minor, features)
SELECT h.id, t.code, t.name, t.description, t.max_guests, t.beds, t.size_sqm, t.image, t.rate, t.features
FROM hotel h
JOIN (VALUES
  ('harborview', 'HKG', 'Harbour King', 'Floor-to-ceiling windows over the fishing boats, a deep tub and a king bed.', 2, '1 king', 32, '/images/rooms/king.webp', 24000, ARRAY['Harbour view', 'Rain shower', 'Espresso machine']),
  ('harborview', 'GTW', 'Garden Twin', 'Two singles facing the lemon garden; quiet, bright and cool in summer.', 2, '2 singles', 28, '/images/rooms/twin.webp', 19000, ARRAY['Garden view', 'Rain shower']),
  ('harborview', 'JST', 'Junior Suite', 'A corner suite with a sitting area, sofa bed and a balcony over the quay.', 3, '1 king + sofa bed', 48, '/images/rooms/suite.webp', 36000, ARRAY['Balcony', 'Harbour view', 'Bathtub', 'Espresso machine']),
  ('harborview', 'FAM', 'Family Loft', 'Split-level loft: a queen upstairs, two singles below, room for everyone''s things.', 4, '1 queen + 2 singles', 55, '/images/rooms/family.webp', 41000, ARRAY['Two levels', 'Kitchenette', 'Garden view']),
  ('alder-quarter', 'DBL', 'Lane Double', 'Above the bookbinders'' lane, with an oak desk and a double bed.', 2, '1 double', 21, '/images/rooms/double.webp', 16500, ARRAY['Oak desk', 'Walk-in shower']),
  ('alder-quarter', 'KNG', 'Merchant King', 'High ceilings, original beams and a king bed in the oldest of the three houses.', 2, '1 king', 30, '/images/rooms/king.webp', 21000, ARRAY['Original beams', 'Bathtub']),
  ('alder-quarter', 'TWN', 'Courtyard Twin', 'Two singles over the courtyard garden, the quietest rooms in town.', 2, '2 singles', 24, '/images/rooms/twin.webp', 17500, ARRAY['Courtyard view']),
  ('alder-quarter', 'STE', 'Library Suite', 'A suite with its own reading room and a fireplace that works.', 3, '1 king + daybed', 44, '/images/rooms/suite.webp', 32000, ARRAY['Fireplace', 'Reading room', 'Bathtub']),
  ('saltmeadow', 'CTG', 'Vine Cottage', 'A stone cottage among the vines with a private terrace.', 2, '1 king', 38, '/images/rooms/cottage.webp', 21000, ARRAY['Private terrace', 'Outdoor shower']),
  ('saltmeadow', 'DUN', 'Dune Cottage', 'The cottages nearest the sea, with a path straight through the dunes.', 2, '1 king', 38, '/images/rooms/cottage.webp', 24500, ARRAY['Sea path', 'Private terrace']),
  ('saltmeadow', 'FAM', 'Farmhouse Family Room', 'Two bedrooms in the old farmhouse, beside the kitchen garden.', 5, '1 king + 3 singles', 62, '/images/rooms/family.webp', 33000, ARRAY['Two bedrooms', 'Garden view', 'Kitchenette']),
  ('saltmeadow', 'TWN', 'Barn Twin', 'Bright twin rooms in the converted grain barn.', 2, '2 singles', 26, '/images/rooms/twin.webp', 16000, ARRAY['Vineyard view']),
  ('summit-house', 'PKG', 'Peak King', 'Glacier-facing king room with a balcony and a boot warmer by the door.', 2, '1 king', 34, '/images/rooms/king.webp', 31000, ARRAY['Glacier view', 'Balcony', 'Boot warmer']),
  ('summit-house', 'CHT', 'Chalet Suite', 'A two-room suite with a wood stove and a soaking tub at the window.', 4, '1 king + bunk beds', 60, '/images/rooms/suite.webp', 52000, ARRAY['Wood stove', 'Soaking tub', 'Glacier view']),
  ('summit-house', 'TWN', 'Alpine Twin', 'Pine-panelled twin room on the village side.', 2, '2 singles', 26, '/images/rooms/twin.webp', 23500, ARRAY['Village view']),
  ('summit-house', 'FAM', 'Family Room', 'A king and two bunks, with ski storage of its own.', 4, '1 king + 2 bunks', 46, '/images/rooms/family.webp', 39000, ARRAY['Ski storage', 'Balcony']),
  ('lantern-row', 'KNG', 'Works King', 'Factory windows, a king bed and a desk wide enough to spread out on.', 2, '1 king', 30, '/images/rooms/king.webp', 27900, ARRAY['Skyline view', 'Work desk', 'Walk-in shower']),
  ('lantern-row', 'QQ', 'Double Queen', 'Two queens under the old crane rail; good for friends and families.', 4, '2 queens', 36, '/images/rooms/double.webp', 31900, ARRAY['Skyline view']),
  ('lantern-row', 'STU', 'Studio', 'A compact studio with a kitchenette, for longer stays.', 2, '1 queen', 24, '/images/rooms/double.webp', 22900, ARRAY['Kitchenette', 'Work desk']),
  ('lantern-row', 'LFT', 'Lantern Loft', 'The top-floor loft with a terrace onto the rooftop bar.', 3, '1 king + sofa bed', 52, '/images/rooms/suite.webp', 49900, ARRAY['Private terrace', 'Skyline view', 'Bathtub'])
) AS t(hotel_slug, code, name, description, max_guests, beds, size_sqm, image, rate, features)
  ON h.slug = t.hotel_slug;

-- Rooms: each type gets 5 to 9 rooms, numbered by floor.
INSERT INTO room (hotel_id, room_type_id, number, floor)
SELECT rt.hotel_id, rt.id, (fl.floor * 100 + row_number() OVER (PARTITION BY rt.hotel_id, fl.floor ORDER BY rt.code, n))::text, fl.floor
FROM room_type rt
CROSS JOIN LATERAL generate_series(1, 5 + abs(hashtext(rt.code || rt.hotel_id::text)) % 5) AS n
CROSS JOIN LATERAL (SELECT 1 + (n - 1) % 4 AS floor) fl;

INSERT INTO guest (first_name, last_name, email, phone)
SELECT f, l, lower(f || '.' || l || '@example.com'), '+1 555 0' || lpad((row_number() OVER ())::text, 3, '0')
FROM unnest(ARRAY['Ada','Bruno','Celia','Dmitri','Elena','Farid','Grace','Hugo','Ines','Jonas','Keiko','Luca',
                  'Maya','Nikolai','Olivia','Pablo','Quinn','Rosa','Sami','Tess','Umar','Vera','Wen','Yara']) AS f
CROSS JOIN unnest(ARRAY['Abbott','Brandt','Castillo','Dubois','Eriksen','Fontaine','Goldberg','Haddad','Ibarra','Jansen',
                        'Kowalski','Lindqvist','Moreau','Nakamura','Okafor','Petrov','Quintero','Rossi','Sato','Varga']) AS l;

INSERT INTO staff_user (username, display_name, password_hash) VALUES
('desk', 'Morgan Ellis', '{bcrypt}' || crypt('kestrel-desk', gen_salt('bf', 10))),
('harbor', 'Rui Antunes', '{bcrypt}' || crypt('kestrel-desk', gen_salt('bf', 10)));
INSERT INTO staff_hotel (staff_id, hotel_id)
SELECT s.id, h.id FROM staff_user s CROSS JOIN hotel h WHERE s.username = 'desk' OR h.slug = 'harborview';

-- Reservations: walk every room from 60 days ago to 120 days ahead, leaving random gaps and
-- placing stays of one to seven nights, priced by the same rules as PricingPolicy.
DO $$
DECLARE
    alphabet constant text := '23456789ABCDEFGHJKMNPQRSTUVWXYZ';
    guest_ids uuid[];
    r record;
    d date;
    nights int;
    code text;
    subtotal bigint;
    discount bigint;
    night date;
    status text;
    adults int;
BEGIN
    PERFORM setseed(0.42);
    SELECT array_agg(id ORDER BY email) INTO guest_ids FROM guest;
    FOR r IN
        SELECT rm.id AS room_id, rm.hotel_id, rt.base_rate_minor, rt.max_guests, h.currency, h.tax_bp
        FROM room rm JOIN room_type rt ON rt.id = rm.room_type_id JOIN hotel h ON h.id = rm.hotel_id
        ORDER BY h.slug, rm.number
    LOOP
        d := current_date - 60 + floor(random() * 5)::int;
        WHILE d < current_date + 120 LOOP
            nights := 1 + floor(power(random(), 1.6) * 7)::int;
            subtotal := 0;
            FOR i IN 0 .. nights - 1 LOOP
                night := d + i;
                subtotal := subtotal + CASE WHEN extract(isodow FROM night) IN (5, 6)
                                            THEN round(r.base_rate_minor * 1.2) ELSE r.base_rate_minor END;
            END LOOP;
            discount := CASE WHEN nights >= 7 THEN round(subtotal * 0.10) ELSE 0 END;
            status := CASE
                WHEN random() < 0.05 THEN 'CANCELLED'
                WHEN d + nights <= current_date THEN 'CHECKED_OUT'
                WHEN d < current_date THEN 'CHECKED_IN'
                ELSE 'BOOKED' END;
            adults := least(r.max_guests, 1 + floor(random() * 2)::int);
            code := 'KV-';
            FOR i IN 1 .. 6 LOOP
                code := code || substr(alphabet, 1 + floor(random() * length(alphabet))::int, 1);
            END LOOP;
            INSERT INTO reservation (confirmation_code, hotel_id, room_id, guest_id, check_in, check_out, adults, children,
                                     status, total_minor, currency, booked_at)
            VALUES (code, r.hotel_id, r.room_id, guest_ids[1 + floor(random() * array_length(guest_ids, 1))::int],
                    d, d + nights, adults, CASE WHEN r.max_guests > adults AND random() < 0.2 THEN 1 ELSE 0 END,
                    status, (subtotal - discount) + round((subtotal - discount) * r.tax_bp / 10000.0), r.currency,
                    (d - (3 + floor(random() * 60)::int))::timestamptz)
            ON CONFLICT (confirmation_code) DO NOTHING;
            d := d + nights + floor(power(random(), 2) * 6)::int;
        END LOOP;
    END LOOP;
END $$;
