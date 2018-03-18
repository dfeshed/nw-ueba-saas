describe('EntityUtils.service', function () {
    'use strict';

    var entityUtils, URLUtils;

    beforeEach(function () {
        module('Fortscale.shared.services.modelUtils');
        module('Fortscale.shared.services.URLUtils');
        module('Fortscale.shared.services.assert');
        module('Fortscale.shared.services.fsModals');
        module('ui.bootstrap');
        module('Config');

        inject(function (_entityUtils_, _URLUtils_) {
            entityUtils = _entityUtils_;
            URLUtils = _URLUtils_;
        });
    });

    describe('Private methods', function () {

    });

    describe('Public methods', function () {
        describe('navigateToEntityProfile', function () {

            it('should call URLUtils.setUrl with "user/entityId /user_overview" and false', function () {

                var mockUrlUtils = spyOn(URLUtils, 'setUrl');

                var userId = 'user1';
                entityUtils.navigateToEntityProfile('User', userId);

                var expectedUrl = 'user/user1/user_overview';
                expect(mockUrlUtils)
                    .toHaveBeenCalledWith(expectedUrl, false);

            });

            it('should not call URLUtils.setUrl  bacuase entity is not user', function () {

                var mockUrlUtils = spyOn(URLUtils, 'setUrl');
                entityUtils.navigateToEntityProfile('Computer', 'comp1');
                expect(mockUrlUtils).not
                    .toHaveBeenCalled();
            });

        });

    });

});
