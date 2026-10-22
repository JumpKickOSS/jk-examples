/** The Material Symbol that pictures an amenity, by keyword; a check mark when none fits. */
const ICONS: [RegExp, string][] = [
  [/pool/i, 'pool'],
  [/spa|hammam|sauna/i, 'spa'],
  [/bar|cocktail/i, 'local_bar'],
  [/breakfast|kitchen|café|cafe|fondue/i, 'restaurant'],
  [/fitness|gym/i, 'fitness_center'],
  [/parking|valet/i, 'local_parking'],
  [/wi-?fi/i, 'wifi'],
  [/library/i, 'local_library'],
  [/garden|courtyard/i, 'yard'],
  [/bike|trail/i, 'directions_bike'],
  [/pet/i, 'pets'],
  [/tasting|cellar|wine/i, 'wine_bar'],
  [/ev charging/i, 'ev_station'],
  [/ski/i, 'downhill_skiing'],
  [/fireside|fire/i, 'fireplace'],
  [/rooftop/i, 'roofing'],
  [/co-working|work/i, 'laptop_mac'],
];

export function amenityIcon(name: string): string {
  return ICONS.find(([pattern]) => pattern.test(name))?.[1] ?? 'check_circle';
}
