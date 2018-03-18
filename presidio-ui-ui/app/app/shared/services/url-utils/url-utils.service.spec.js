describe('URLUtils.service', function () {
    'use strict';

    var URLUtils;

    beforeEach(module('Fortscale.shared.services.URLUtils'));

    beforeEach(inject(function (_URLUtils_) {
        URLUtils = _URLUtils_;
    }));


    describe('Private methods', function () {

    });

    describe('Public methods', function () {
        describe('getSearchQueryString', function () {
            var mockLocationUrl;

            beforeEach(function () {
                mockLocationUrl = spyOn(URLUtils.$location, 'url').and
                    .returnValue('/some/path?foo=bar&baz=xoxo#hashValue');
            });

            it('should return the query string without the hash when there is a url', function () {

                expect(URLUtils.getSearchQueryString()).toBe('?foo=bar&baz=xoxo');
            });

            it('should return the query string without the hash and the question mark ' +
                'if there is a url and withoutQuestionMark is true', function () {

                expect(URLUtils.getSearchQueryString(true)).toBe('foo=bar&baz=xoxo');
            });
        });

        describe('setUrl', function () {
            var url;
            var queryString;

            beforeEach(function () {
                url = '/someUrl';
                queryString = '?someQueryString';

                spyOn(URLUtils.$location, 'url');
                spyOn(URLUtils, 'getSearchQueryString').and.returnValue(queryString);
            });

            it('should invoke $location.url with the provided url', function () {
                URLUtils.setUrl(url);
                expect(URLUtils.$location.url).toHaveBeenCalledWith(url);
            });

            it('should invoke $location.url with the provided url ' +
                'and the search query string if shouldPassQuery is set to true', function () {
                URLUtils.setUrl(url, true);
                expect(URLUtils.$location.url).toHaveBeenCalledWith(url + queryString);
            });
        });

    });

});
