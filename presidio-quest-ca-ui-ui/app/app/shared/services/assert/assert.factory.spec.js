(function () {
    'use strict';

    describe('assert.factory', function () {

    	var assert;

    	beforeEach(function () {
    		module('Fortscale.shared.services.assert');

			inject(function (_assert_) {
				assert = _assert_;
			});
    	});

		it('should be a function', function () {
			expect(typeof assert === 'function').toBe(true);
		});

		it('should not throw if condition is true', function () {
			function testFunc () {
				assert(true);
			}

			expect(testFunc).not.toThrow();
		});

		it('should throw when condition is false', function () {
			function testFunc () {
				assert(false);
			}

			expect(testFunc).toThrow();
		});

		it('should throw with a message if message is passed', function () {
			function testFunc () {
				assert(false, 'some message');
			}

			expect(testFunc).toThrowError('some message');
		});

		it('should throw with a specific type if type is provided', function () {
			function testFunc () {
				assert(false, 'some message', TypeError);
			}

			expect(testFunc).toThrowError(TypeError);
		});
    });
}());
