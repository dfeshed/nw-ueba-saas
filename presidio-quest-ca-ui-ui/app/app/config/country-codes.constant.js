(function () {
    'use strict';

    var countryCodes = {
        "afghanistan": {"name": "Afghanistan", "alpha-2": "AF", "country-code": "004"},
        "åland islands": {"name": "Åland Islands", "alpha-2": "AX", "country-code": "248"},
        "albania": {"name": "Albania", "alpha-2": "AL", "country-code": "008"},
        "algeria": {"name": "Algeria", "alpha-2": "DZ", "country-code": "012"},
        "american samoa": {"name": "American Samoa", "alpha-2": "AS", "country-code": "016"},
        "andorra": {"name": "Andorra", "alpha-2": "AD", "country-code": "020"},
        "angola": {"name": "Angola", "alpha-2": "AO", "country-code": "024"},
        "anguilla": {"name": "Anguilla", "alpha-2": "AI", "country-code": "660"},
        "antarctica": {"name": "Antarctica", "alpha-2": "AQ", "country-code": "010"},
        "antigua and barbuda": {"name": "Antigua and Barbuda", "alpha-2": "AG", "country-code": "028"},
        "argentina": {"name": "Argentina", "alpha-2": "AR", "country-code": "032"},
        "armenia": {"name": "Armenia", "alpha-2": "AM", "country-code": "051"},
        "aruba": {"name": "Aruba", "alpha-2": "AW", "country-code": "533"},
        "australia": {"name": "Australia", "alpha-2": "AU", "country-code": "036"},
        "austria": {"name": "Austria", "alpha-2": "AT", "country-code": "040"},
        "azerbaijan": {"name": "Azerbaijan", "alpha-2": "AZ", "country-code": "031"},
        "bahamas": {"name": "Bahamas", "alpha-2": "BS", "country-code": "044"},
        "bahrain": {"name": "Bahrain", "alpha-2": "BH", "country-code": "048"},
        "bangladesh": {"name": "Bangladesh", "alpha-2": "BD", "country-code": "050"},
        "barbados": {"name": "Barbados", "alpha-2": "BB", "country-code": "052"},
        "belarus": {"name": "Belarus", "alpha-2": "BY", "country-code": "112"},
        "belgium": {"name": "Belgium", "alpha-2": "BE", "country-code": "056"},
        "belize": {"name": "Belize", "alpha-2": "BZ", "country-code": "084"},
        "benin": {"name": "Benin", "alpha-2": "BJ", "country-code": "204"},
        "bermuda": {"name": "Bermuda", "alpha-2": "BM", "country-code": "060"},
        "bhutan": {"name": "Bhutan", "alpha-2": "BT", "country-code": "064"},
        "bolivia (plurinational state of)": {
            "name": "Bolivia (Plurinational State of)",
            "alpha-2": "BO",
            "country-code": "068"
        },
        "bolivia": { "name": "Bolivia", "alpha-2": "BO", "country-code": "068"},
        "bonaire, sint eustatius and saba": {
            "name": "Bonaire, Sint Eustatius and Saba",
            "alpha-2": "BQ",
            "country-code": "535"
        },
        "bosnia and herzegovina": {"name": "Bosnia and Herzegovina", "alpha-2": "BA", "country-code": "070"},
        "botswana": {"name": "Botswana", "alpha-2": "BW", "country-code": "072"},
        "bouvet island": {"name": "Bouvet Island", "alpha-2": "BV", "country-code": "074"},
        "brazil": {"name": "Brazil", "alpha-2": "BR", "country-code": "076"},
        "british indian ocean territory": {
            "name": "British Indian Ocean Territory",
            "alpha-2": "IO",
            "country-code": "086"
        },
        "brunei darussalam": {"name": "Brunei Darussalam", "alpha-2": "BN", "country-code": "096"},
        "bulgaria": {"name": "Bulgaria", "alpha-2": "BG", "country-code": "100"},
        "burkina faso": {"name": "Burkina Faso", "alpha-2": "BF", "country-code": "854"},
        "burundi": {"name": "Burundi", "alpha-2": "BI", "country-code": "108"},
        "cambodia": {"name": "Cambodia", "alpha-2": "KH", "country-code": "116"},
        "cameroon": {"name": "Cameroon", "alpha-2": "CM", "country-code": "120"},
        "canada": {"name": "Canada", "alpha-2": "CA", "country-code": "124"},
        "cabo verde": {"name": "Cabo Verde", "alpha-2": "CV", "country-code": "132"},
        "cape verde": {"name": "Cape Verde", "alpha-2": "CV", "country-code": "132"},
        "cayman islands": {"name": "Cayman Islands", "alpha-2": "KY", "country-code": "136"},
        "central african republic": {"name": "Central African Republic", "alpha-2": "CF", "country-code": "140"},
        "chad": {"name": "Chad", "alpha-2": "TD", "country-code": "148"},
        "chile": {"name": "Chile", "alpha-2": "CL", "country-code": "152"},
        "china": {"name": "China", "alpha-2": "CN", "country-code": "156"},
        "christmas island": {"name": "Christmas Island", "alpha-2": "CX", "country-code": "162"},
        "cocos (keeling) islands": {"name": "Cocos (Keeling) Islands", "alpha-2": "CC", "country-code": "166"},
        "colombia": {"name": "Colombia", "alpha-2": "CO", "country-code": "170"},
        "comoros": {"name": "Comoros", "alpha-2": "KM", "country-code": "174"},
        "congo": {"name": "Congo", "alpha-2": "CG", "country-code": "178"},
        "congo (democratic republic of the)": {
            "name": "Congo (Democratic Republic of the)",
            "alpha-2": "CD",
            "country-code": "180"
        },
        "congo the democratic republic of the": {
                    "name": "Congo The Democratic Republic Of The",
                    "alpha-2": "CD",
                    "country-code": "180"
                },
        "cook islands": {"name": "Cook Islands", "alpha-2": "CK", "country-code": "184"},
        "costa rica": {"name": "Costa Rica", "alpha-2": "CR", "country-code": "188"},
        "côte d'ivoire": {"name": "Côte d'Ivoire", "alpha-2": "CI", "country-code": "384"},
        "cote d'ivoire": {"name": "Cote d'Ivoire", "alpha-2": "CI", "country-code": "384"},
        "croatia": {"name": "Croatia", "alpha-2": "HR", "country-code": "191"},
        "cuba": {"name": "Cuba", "alpha-2": "CU", "country-code": "192"},
        "curaçao": {"name": "Curaçao", "alpha-2": "CW", "country-code": "531"},
        "cyprus": {"name": "Cyprus", "alpha-2": "CY", "country-code": "196"},
        "czech republic": {"name": "Czech Republic", "alpha-2": "CZ", "country-code": "203"},
        "denmark": {"name": "Denmark", "alpha-2": "DK", "country-code": "208"},
        "djibouti": {"name": "Djibouti", "alpha-2": "DJ", "country-code": "262"},
        "dominica": {"name": "Dominica", "alpha-2": "DM", "country-code": "212"},
        "dominican republic": {"name": "Dominican Republic", "alpha-2": "DO", "country-code": "214"},
        "ecuador": {"name": "Ecuador", "alpha-2": "EC", "country-code": "218"},
        "egypt": {"name": "Egypt", "alpha-2": "EG", "country-code": "818"},
        "el salvador": {"name": "El Salvador", "alpha-2": "SV", "country-code": "222"},
        "equatorial guinea": {"name": "Equatorial Guinea", "alpha-2": "GQ", "country-code": "226"},
        "eritrea": {"name": "Eritrea", "alpha-2": "ER", "country-code": "232"},
        "estonia": {"name": "Estonia", "alpha-2": "EE", "country-code": "233"},
        "ethiopia": {"name": "Ethiopia", "alpha-2": "ET", "country-code": "231"},
        "falkland islands (malvinas)": {"name": "Falkland Islands (Malvinas)", "alpha-2": "FK", "country-code": "238"},
        "faroe islands": {"name": "Faroe Islands", "alpha-2": "FO", "country-code": "234"},
        "fiji": {"name": "Fiji", "alpha-2": "FJ", "country-code": "242"},
        "finland": {"name": "Finland", "alpha-2": "FI", "country-code": "246"},
        "france": {"name": "France", "alpha-2": "FR", "country-code": "250"},
        "french guiana": {"name": "French Guiana", "alpha-2": "GF", "country-code": "254"},
        "french polynesia": {"name": "French Polynesia", "alpha-2": "PF", "country-code": "258"},
        "french southern territories": {"name": "French Southern Territories", "alpha-2": "TF", "country-code": "260"},
        "gabon": {"name": "Gabon", "alpha-2": "GA", "country-code": "266"},
        "gambia": {"name": "Gambia", "alpha-2": "GM", "country-code": "270"},
        "georgia": {"name": "Georgia", "alpha-2": "GE", "country-code": "268"},
        "germany": {"name": "Germany", "alpha-2": "DE", "country-code": "276"},
        "ghana": {"name": "Ghana", "alpha-2": "GH", "country-code": "288"},
        "gibraltar": {"name": "Gibraltar", "alpha-2": "GI", "country-code": "292"},
        "greece": {"name": "Greece", "alpha-2": "GR", "country-code": "300"},
        "greenland": {"name": "Greenland", "alpha-2": "GL", "country-code": "304"},
        "grenada": {"name": "Grenada", "alpha-2": "GD", "country-code": "308"},
        "guadeloupe": {"name": "Guadeloupe", "alpha-2": "GP", "country-code": "312"},
        "guam": {"name": "Guam", "alpha-2": "GU", "country-code": "316"},
        "guatemala": {"name": "Guatemala", "alpha-2": "GT", "country-code": "320"},
        "guernsey": {"name": "Guernsey", "alpha-2": "GG", "country-code": "831"},
        "guinea": {"name": "Guinea", "alpha-2": "GN", "country-code": "324"},
        "guinea-bissau": {"name": "Guinea-Bissau", "alpha-2": "GW", "country-code": "624"},
        "guyana": {"name": "Guyana", "alpha-2": "GY", "country-code": "328"},
        "haiti": {"name": "Haiti", "alpha-2": "HT", "country-code": "332"},
        "heard island and mcdonald islands": {
            "name": "Heard Island and McDonald Islands",
            "alpha-2": "HM",
            "country-code": "334"
        },
        "holy see": {"name": "Holy See", "alpha-2": "VA", "country-code": "336"},
        "honduras": {"name": "Honduras", "alpha-2": "HN", "country-code": "340"},
        "hong kong": {"name": "Hong Kong", "alpha-2": "HK", "country-code": "344"},
        "hungary": {"name": "Hungary", "alpha-2": "HU", "country-code": "348"},
        "iceland": {"name": "Iceland", "alpha-2": "IS", "country-code": "352"},
        "india": {"name": "India", "alpha-2": "IN", "country-code": "356"},
        "indonesia": {"name": "Indonesia", "alpha-2": "ID", "country-code": "360"},
        "iran (islamic republic of)": {"name": "Iran (Islamic Republic of)", "alpha-2": "IR", "country-code": "364"},
        "iran islamic republic of": {"name": "Iran Islamic Republic Of", "alpha-2": "IR", "country-code": "364"},
        "iraq": {"name": "Iraq", "alpha-2": "IQ", "country-code": "368"},
        "ireland": {"name": "Ireland", "alpha-2": "IE", "country-code": "372"},
        "isle of man": {"name": "Isle of Man", "alpha-2": "IM", "country-code": "833"},
        "israel": {"name": "Israel", "alpha-2": "IL", "country-code": "376"},
        "italy": {"name": "Italy", "alpha-2": "IT", "country-code": "380"},
        "jamaica": {"name": "Jamaica", "alpha-2": "JM", "country-code": "388"},
        "japan": {"name": "Japan", "alpha-2": "JP", "country-code": "392"},
        "jersey": {"name": "Jersey", "alpha-2": "JE", "country-code": "832"},
        "jordan": {"name": "Jordan", "alpha-2": "JO", "country-code": "400"},
        "kazakhstan": {"name": "Kazakhstan", "alpha-2": "KZ", "country-code": "398"},
        "kenya": {"name": "Kenya", "alpha-2": "KE", "country-code": "404"},
        "kiribati": {"name": "Kiribati", "alpha-2": "KI", "country-code": "296"},
        "korea (democratic people's republic of)": {
            "name": "Korea (Democratic People's Republic of)",
            "alpha-2": "KP",
            "country-code": "408"
        },
        "korea (republic of)": {"name": "Korea (Republic of)", "alpha-2": "KR", "country-code": "410"},
        "korea republic of": {"name": "Korea Republic Of", "alpha-2": "KR", "country-code": "410"},
        "kuwait": {"name": "Kuwait", "alpha-2": "KW", "country-code": "414"},
        "kyrgyzstan": {"name": "Kyrgyzstan", "alpha-2": "KG", "country-code": "417"},
        "lao people's democratic republic": {
            "name": "Lao People's Democratic Republic",
            "alpha-2": "LA",
            "country-code": "418"
        },
        "latvia": {"name": "Latvia", "alpha-2": "LV", "country-code": "428"},
        "lebanon": {"name": "Lebanon", "alpha-2": "LB", "country-code": "422"},
        "lesotho": {"name": "Lesotho", "alpha-2": "LS", "country-code": "426"},
        "liberia": {"name": "Liberia", "alpha-2": "LR", "country-code": "430"},
        "libya": {"name": "Libya", "alpha-2": "LY", "country-code": "434"},
        "liechtenstein": {"name": "Liechtenstein", "alpha-2": "LI", "country-code": "438"},
        "lithuania": {"name": "Lithuania", "alpha-2": "LT", "country-code": "440"},
        "luxembourg": {"name": "Luxembourg", "alpha-2": "LU", "country-code": "442"},
        "macao": {"name": "Macao", "alpha-2": "MO", "country-code": "446"},
        "macedonia (the former yugoslav republic of)": {
            "name": "Macedonia (the former Yugoslav Republic of)",
            "alpha-2": "MK",
            "country-code": "807"
        },
        "macedonia the former yugoslav republic of": {
            "name": "Macedonia The Former Yugoslav Republic Of",
            "alpha-2": "MK",
            "country-code": "807"
        },
        "madagascar": {"name": "Madagascar", "alpha-2": "MG", "country-code": "450"},
        "malawi": {"name": "Malawi", "alpha-2": "MW", "country-code": "454"},
        "malaysia": {"name": "Malaysia", "alpha-2": "MY", "country-code": "458"},
        "maldives": {"name": "Maldives", "alpha-2": "MV", "country-code": "462"},
        "mali": {"name": "Mali", "alpha-2": "ML", "country-code": "466"},
        "malta": {"name": "Malta", "alpha-2": "MT", "country-code": "470"},
        "marshall islands": {"name": "Marshall Islands", "alpha-2": "MH", "country-code": "584"},
        "martinique": {"name": "Martinique", "alpha-2": "MQ", "country-code": "474"},
        "mauritania": {"name": "Mauritania", "alpha-2": "MR", "country-code": "478"},
        "mauritius": {"name": "Mauritius", "alpha-2": "MU", "country-code": "480"},
        "mayotte": {"name": "Mayotte", "alpha-2": "YT", "country-code": "175"},
        "mexico": {"name": "Mexico", "alpha-2": "MX", "country-code": "484"},
        "micronesia (federated states of)": {
            "name": "Micronesia (Federated States of)",
            "alpha-2": "FM",
            "country-code": "583"
        },
        "micronesia federated states of": {
            "name": "Micronesia Federated States Of",
            "alpha-2": "FM",
            "country-code": "583"
        },
        "moldova (republic of)": {"name": "Moldova (Republic of)", "alpha-2": "MD", "country-code": "498"},
        "moldova republic of": {"name": "Moldova Republic Of", "alpha-2": "MD", "country-code": "498"},
        "monaco": {"name": "Monaco", "alpha-2": "MC", "country-code": "492"},
        "mongolia": {"name": "Mongolia", "alpha-2": "MN", "country-code": "496"},
        "montenegro": {"name": "Montenegro", "alpha-2": "ME", "country-code": "499"},
        "montserrat": {"name": "Montserrat", "alpha-2": "MS", "country-code": "500"},
        "morocco": {"name": "Morocco", "alpha-2": "MA", "country-code": "504"},
        "mozambique": {"name": "Mozambique", "alpha-2": "MZ", "country-code": "508"},
        "myanmar": {"name": "Myanmar", "alpha-2": "MM", "country-code": "104"},
        "namibia": {"name": "Namibia", "alpha-2": "NA", "country-code": "516"},
        "nauru": {"name": "Nauru", "alpha-2": "NR", "country-code": "520"},
        "nepal": {"name": "Nepal", "alpha-2": "NP", "country-code": "524"},
        "netherlands": {"name": "Netherlands", "alpha-2": "NL", "country-code": "528"},
        "new caledonia": {"name": "New Caledonia", "alpha-2": "NC", "country-code": "540"},
        "new zealand": {"name": "New Zealand", "alpha-2": "NZ", "country-code": "554"},
        "nicaragua": {"name": "Nicaragua", "alpha-2": "NI", "country-code": "558"},
        "niger": {"name": "Niger", "alpha-2": "NE", "country-code": "562"},
        "nigeria": {"name": "Nigeria", "alpha-2": "NG", "country-code": "566"},
        "niue": {"name": "Niue", "alpha-2": "NU", "country-code": "570"},
        "norfolk island": {"name": "Norfolk Island", "alpha-2": "NF", "country-code": "574"},
        "northern mariana islands": {"name": "Northern Mariana Islands", "alpha-2": "MP", "country-code": "580"},
        "norway": {"name": "Norway", "alpha-2": "NO", "country-code": "578"},
        "oman": {"name": "Oman", "alpha-2": "OM", "country-code": "512"},
        "pakistan": {"name": "Pakistan", "alpha-2": "PK", "country-code": "586"},
        "palau": {"name": "Palau", "alpha-2": "PW", "country-code": "585"},
        "palestine, state of": {"name": "Palestine, State of", "alpha-2": "PS", "country-code": "275"},
        "panama": {"name": "Panama", "alpha-2": "PA", "country-code": "591"},
        "papua new guinea": {"name": "Papua New Guinea", "alpha-2": "PG", "country-code": "598"},
        "paraguay": {"name": "Paraguay", "alpha-2": "PY", "country-code": "600"},
        "peru": {"name": "Peru", "alpha-2": "PE", "country-code": "604"},
        "philippines": {"name": "Philippines", "alpha-2": "PH", "country-code": "608"},
        "pitcairn": {"name": "Pitcairn", "alpha-2": "PN", "country-code": "612"},
        "poland": {"name": "Poland", "alpha-2": "PL", "country-code": "616"},
        "portugal": {"name": "Portugal", "alpha-2": "PT", "country-code": "620"},
        "puerto rico": {"name": "Puerto Rico", "alpha-2": "PR", "country-code": "630"},
        "qatar": {"name": "Qatar", "alpha-2": "QA", "country-code": "634"},
        "réunion": {"name": "Réunion", "alpha-2": "RE", "country-code": "638"},
        "reunion": {"name": "Reunion", "alpha-2": "RE", "country-code": "638"},
        "romania": {"name": "Romania", "alpha-2": "RO", "country-code": "642"},
        "russian federation": {"name": "Russian Federation", "alpha-2": "RU", "country-code": "643"},
        "rwanda": {"name": "Rwanda", "alpha-2": "RW", "country-code": "646"},
        "saint barthélemy": {"name": "Saint Barthélemy", "alpha-2": "BL", "country-code": "652"},
        "saint helena, ascension and tristan da cunha": {
            "name": "Saint Helena, Ascension and Tristan da Cunha",
            "alpha-2": "SH",
            "country-code": "654"
        },
        "saint kitts and nevis": {"name": "Saint Kitts and Nevis", "alpha-2": "KN", "country-code": "659"},
        "saint lucia": {"name": "Saint Lucia", "alpha-2": "LC", "country-code": "662"},
        "saint martin (french part)": {"name": "Saint Martin (French part)", "alpha-2": "MF", "country-code": "663"},
        "saint pierre and miquelon": {"name": "Saint Pierre and Miquelon", "alpha-2": "PM", "country-code": "666"},
        "saint vincent and the grenadines": {
            "name": "Saint Vincent and the Grenadines",
            "alpha-2": "VC",
            "country-code": "670"
        },
        "samoa": {"name": "Samoa", "alpha-2": "WS", "country-code": "882"},
        "san marino": {"name": "San Marino", "alpha-2": "SM", "country-code": "674"},
        "sao tome and principe": {"name": "Sao Tome and Principe", "alpha-2": "ST", "country-code": "678"},
        "saudi arabia": {"name": "Saudi Arabia", "alpha-2": "SA", "country-code": "682"},
        "senegal": {"name": "Senegal", "alpha-2": "SN", "country-code": "686"},
        "serbia": {"name": "Serbia", "alpha-2": "RS", "country-code": "688"},
        "serbia and montenegro": {"name": "Serbia and Montenegro", "alpha-2": "RS", "country-code": "688"},
        "seychelles": {"name": "Seychelles", "alpha-2": "SC", "country-code": "690"},
        "sierra leone": {"name": "Sierra Leone", "alpha-2": "SL", "country-code": "694"},
        "singapore": {"name": "Singapore", "alpha-2": "SG", "country-code": "702"},
        "sint maarten (dutch part)": {"name": "Sint Maarten (Dutch part)", "alpha-2": "SX", "country-code": "534"},
        "slovakia": {"name": "Slovakia", "alpha-2": "SK", "country-code": "703"},
        "slovenia": {"name": "Slovenia", "alpha-2": "SI", "country-code": "705"},
        "solomon islands": {"name": "Solomon Islands", "alpha-2": "SB", "country-code": "090"},
        "somalia": {"name": "Somalia", "alpha-2": "SO", "country-code": "706"},
        "south africa": {"name": "South Africa", "alpha-2": "ZA", "country-code": "710"},
        "south georgia and the south sandwich islands": {
            "name": "South Georgia and the South Sandwich Islands",
            "alpha-2": "GS",
            "country-code": "239"
        },
        "south sudan": {"name": "South Sudan", "alpha-2": "SS", "country-code": "728"},
        "spain": {"name": "Spain", "alpha-2": "ES", "country-code": "724"},
        "sri lanka": {"name": "Sri Lanka", "alpha-2": "LK", "country-code": "144"},
        "sudan": {"name": "Sudan", "alpha-2": "SD", "country-code": "729"},
        "suriname": {"name": "Suriname", "alpha-2": "SR", "country-code": "740"},
        "svalbard and jan mayen": {"name": "Svalbard and Jan Mayen", "alpha-2": "SJ", "country-code": "744"},
        "swaziland": {"name": "Swaziland", "alpha-2": "SZ", "country-code": "748"},
        "sweden": {"name": "Sweden", "alpha-2": "SE", "country-code": "752"},
        "switzerland": {"name": "Switzerland", "alpha-2": "CH", "country-code": "756"},
        "syrian arab republic": {"name": "Syrian Arab Republic", "alpha-2": "SY", "country-code": "760"},
        "taiwan, province of china": {"name": "Taiwan, Province of China", "alpha-2": "TW", "country-code": "158"},
        "taiwan province of china": {"name": "Taiwan Province Of China", "alpha-2": "TW", "country-code": "158"},
        "taiwan": {"name": "Taiwan", "alpha-2": "TW", "country-code": "158"},
        "tajikistan": {"name": "Tajikistan", "alpha-2": "TJ", "country-code": "762"},
        "tanzania, united republic of": {
            "name": "Tanzania, United Republic of",
            "alpha-2": "TZ",
            "country-code": "834"
        },
        "thailand": {"name": "Thailand", "alpha-2": "TH", "country-code": "764"},
        "timor-leste": {"name": "Timor-Leste", "alpha-2": "TL", "country-code": "626"},
        "togo": {"name": "Togo", "alpha-2": "TG", "country-code": "768"},
        "tokelau": {"name": "Tokelau", "alpha-2": "TK", "country-code": "772"},
        "tonga": {"name": "Tonga", "alpha-2": "TO", "country-code": "776"},
        "trinidad and tobago": {"name": "Trinidad and Tobago", "alpha-2": "TT", "country-code": "780"},
        "tunisia": {"name": "Tunisia", "alpha-2": "TN", "country-code": "788"},
        "turkey": {"name": "Turkey", "alpha-2": "TR", "country-code": "792"},
        "turkmenistan": {"name": "Turkmenistan", "alpha-2": "TM", "country-code": "795"},
        "turks and caicos islands": {"name": "Turks and Caicos Islands", "alpha-2": "TC", "country-code": "796"},
        "tuvalu": {"name": "Tuvalu", "alpha-2": "TV", "country-code": "798"},
        "uganda": {"name": "Uganda", "alpha-2": "UG", "country-code": "800"},
        "ukraine": {"name": "Ukraine", "alpha-2": "UA", "country-code": "804"},
        "united arab emirates": {"name": "United Arab Emirates", "alpha-2": "AE", "country-code": "784"},
        "united kingdom of great britain and northern ireland": {
            "name": "United Kingdom of Great Britain and Northern Ireland",
            "alpha-2": "GB",
            "country-code": "826"
        },
        "united kingdom": {
            "name": "United Kingdom",
            "alpha-2": "GB",
            "country-code": "826"
        },
        "united states of america": {"name": "United States of America", "alpha-2": "US", "country-code": "840"},
        "usa": {"name": "USA", "alpha-2": "US", "country-code": "840"},
        "united states": {"name": "United States", "alpha-2": "US", "country-code": "840"},
        "united states minor outlying islands": {
            "name": "United States Minor Outlying Islands",
            "alpha-2": "UM",
            "country-code": "581"
        },
        "uruguay": {"name": "Uruguay", "alpha-2": "UY", "country-code": "858"},
        "uzbekistan": {"name": "Uzbekistan", "alpha-2": "UZ", "country-code": "860"},
        "vanuatu": {"name": "Vanuatu", "alpha-2": "VU", "country-code": "548"},
        "venezuela (bolivarian republic of)": {
            "name": "Venezuela (Bolivarian Republic of)",
            "alpha-2": "VE",
            "country-code": "862"
        },
        "venezuela": {"name": "Venezuela", "alpha-2": "VE", "country-code": "862"},
        "viet nam": {"name": "Viet Nam", "alpha-2": "VN", "country-code": "704"},
        "virgin islands (british)": {"name": "Virgin Islands (British)", "alpha-2": "VG", "country-code": "092"},
        "virgin islands (u.s.)": {"name": "Virgin Islands (U.S.)", "alpha-2": "VI", "country-code": "850"},
        "wallis and futuna": {"name": "Wallis and Futuna", "alpha-2": "WF", "country-code": "876"},
        "western sahara": {"name": "Western Sahara", "alpha-2": "EH", "country-code": "732"},
        "yemen": {"name": "Yemen", "alpha-2": "YE", "country-code": "887"},
        "zambia": {"name": "Zambia", "alpha-2": "ZM", "country-code": "894"},
        "zimbabwe": {"name": "Zimbabwe", "alpha-2": "ZW", "country-code": "716"}
    };

    angular.module('Config')
        .constant('countryCodes', countryCodes);
}());
