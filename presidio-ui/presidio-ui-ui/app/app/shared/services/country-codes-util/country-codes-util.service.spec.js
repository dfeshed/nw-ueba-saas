describe('CSVConverter.service', function () {
    'use strict';

    var countryCodesUtil;

    beforeEach(module('Config'));
    beforeEach(module('Fortscale.shared.services.assert'));
    beforeEach(module('Fortscale.shared.services.countryCodesUtil'));

    beforeEach(inject(function (_countryCodesUtil_) {
        countryCodesUtil = _countryCodesUtil_;
    }));

    beforeEach(function () {

    });
    describe('Private methods', function () {
        describe('_getCountryObj', function () {
            it('should return null when called with a non existing name', function () {
                expect(countryCodesUtil._getCountryObj('name', 'nonexisting')).toBe(null);
            });

            it('should return albania country object when name is albania', function () {
                expect(JSON.stringify(countryCodesUtil._getCountryObj('name', 'albania'))).toEqual(JSON.stringify(
                    {"name": "Albania", "alpha-2": "AL", "country-code": "008"}
                ));
            });

            it('should return algeria country object when alpha-2 is DZ', function () {
                expect(JSON.stringify(countryCodesUtil._getCountryObj('alpha-2', 'DZ'))).toEqual(JSON.stringify(
                    {"name": "Algeria", "alpha-2": "DZ", "country-code": "012"}
                ));
            });

            it('should return angola when country-code is 024', function () {
                expect(JSON.stringify(countryCodesUtil._getCountryObj('country-code', '024'))).toEqual(JSON.stringify(
                    {"name": "Angola", "alpha-2": "AO", "country-code": "024"}
                ));
            });

            it('should return null when searchBy points to a non existing property', function () {
                expect(countryCodesUtil._getCountryObj('nonexistingproperty', '024')).toBe(null);
            });

            it('should return null when value points to a non existing value', function () {
                expect(countryCodesUtil._getCountryObj('country-code', '000')).toBe(null);
            });
        });
    });

    describe('Public methods', function () {
        describe('getAlpha2ByCountryName', function () {
            it('should return AW when called with "Aruba"', function () {
                expect(countryCodesUtil.getAlpha2ByCountryName("Aruba")).toBe("AW");
            });

            it('should return AW when called with "aruba"', function () {
                expect(countryCodesUtil.getAlpha2ByCountryName("Aruba")).toBe("AW");
            });

            it('should return null when called with a non existing country name', function () {
                expect(countryCodesUtil.getAlpha2ByCountryName("NonExisting")).toBe(null);
            });
        });

        describe('getCountryCodeByCountryName', function () {
            it('should return 036 when called with "Australia"', function () {
                expect(countryCodesUtil.getCountryCodeByCountryName("Australia")).toBe("036");
            });

            it('should return 036 when called with "australia"', function () {
                expect(countryCodesUtil.getCountryCodeByCountryName("australia")).toBe("036");
            });

            it('should return null when called with a non existing country name', function () {
                expect(countryCodesUtil.getCountryCodeByCountryName("NonExisting")).toBe(null);

            });
        });

        describe('getCountryNameByAlpha2', function () {
            it('should return "Aruba" when called with "AW"', function () {
                expect(countryCodesUtil.getCountryNameByAlpha2("AW")).toBe("Aruba");
            });

            it('should return "Aruba" when called with "aw"', function () {
                expect(countryCodesUtil.getCountryNameByAlpha2("aw")).toBe("Aruba");
            });

            it('should return null when called with a non existing country name', function () {
                expect(countryCodesUtil.getCountryNameByAlpha2("NonExisting")).toBe(null);
            });
        });

        describe('getCountryNameByCountryCode', function () {
            it('should return "Aruba" when provided with 533', function () {
                expect(countryCodesUtil.getCountryNameByCountryCode("533")).toBe("Aruba");
            });

            it('should return null when provided with a non existing country code', function () {
                expect(countryCodesUtil.getCountryNameByCountryCode("000")).toBe(null);
            });
        });
    });

});
