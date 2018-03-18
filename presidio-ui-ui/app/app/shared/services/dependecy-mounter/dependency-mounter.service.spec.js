describe('dependecy-mounter.service', function () {
	'use strict';

	var dependencyMounter;
	var $injector;
	var $http;


	beforeEach(module('Fortscale.shared.services.dependencyMounter'));

	beforeEach(inject(function (_dependencyMounter_, _$injector_, _$http_) {
		dependencyMounter = _dependencyMounter_;
		$injector = _$injector_;
		$http = _$http_;
	}));

	it('should be defined', function () {
		expect(dependencyMounter).toBeDefined();
	});

	it('should have _errorMsgStart that equals Fortscale.shared.services.dependencyMounter: ', function () {
		expect(dependencyMounter._errorMsgStart).toBe('Fortscale.shared.services.dependencyMounter: ');
	});

	describe('validations', function () {
		function getErrorMsgStart (methodName) {
			return dependencyMounter._errorMsgStart + methodName + ': ';
		}

		describe('_validateConstructor', function () {
			it('should throw ReferenceError if Constructor argument is undefined', function () {
				function testFunc () {
					dependencyMounter._validateConstructor('someMethodName', undefined);
				}

				expect(testFunc).toThrowError(ReferenceError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'Constructor argument must be provided.');

			});

			it('should throw TypeError if Constructor argument is not a function', function () {
				function testFunc () {
					dependencyMounter._validateConstructor('someMethodName', {});
				}

				expect(testFunc).toThrowError(TypeError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'Constructor argument must be a function.');

			});

		});

		describe('_validateInstance', function () {
			it('should throw ReferenceError if instance argument is undefined', function () {
				function testFunc () {
					dependencyMounter._validateInstance('someMethodName', undefined);
				}

				expect(testFunc).toThrowError(ReferenceError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'instance argument must be provided.');

			});

			it('should throw TypeError if instance argument is not an object', function () {
				function testFunc () {
					dependencyMounter._validateInstance('someMethodName', 'notAnObject');
				}

				expect(testFunc).toThrowError(TypeError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'instance argument must be an object.');

			});

		});

		describe('_validateArrDependencies', function () {
			it('should throw ReferenceError if arrDependencies argument is undefined', function () {
				function testFunc () {
					dependencyMounter._validateArrDependencies('someMethodName', undefined);
				}

				expect(testFunc).toThrowError(ReferenceError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'arrDependencies argument must be provided.');

			});

			it('should throw TypeError if instance argument is not an object', function () {
				function testFunc () {
					dependencyMounter._validateArrDependencies('someMethodName', 'notAnArray');
				}

				expect(testFunc).toThrowError(TypeError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'arrDependencies argument must be an array.');

			});

			it('should throw TypeError if any member is not a string', function () {
				function testFunc () {
					dependencyMounter._validateArrDependencies('someMethodName', [
						'string',
						'string',
						function notAString () {},
						'string'
					]);

				}

				expect(testFunc).toThrowError(TypeError);
				expect(testFunc).toThrowError(getErrorMsgStart('someMethodName') +
					'arrDependencies array member 2 is not a string.' +
					' All members must be strings.');
			});

		});
	});

	describe('private methods', function () {

		describe('_mountOnObject', function () {
			var angularIsDefinedMock;
			var dependencyNames;
			var mountee;

			beforeEach(function () {
				angularIsDefinedMock = spyOn(angular, 'isDefined').and.returnValue(false);
				spyOn($injector, 'get').and.callThrough();
				dependencyNames = ['$http', '$http', '$http'];
				mountee = {};
			});

			it('should invoke angular.isDefined 3 times (one for each service name)', function () {
				dependencyMounter._mountOnObject(mountee, dependencyNames);
				expect(angular.isDefined.calls.count()).toBe(3);
			});

			it('should invoke $injector.get with the dependency name', function () {
				dependencyMounter._mountOnObject(mountee, dependencyNames);
				expect($injector.get).toHaveBeenCalledWith(dependencyNames[0]);
			});

			it('should put the injected service on the provided mountee object', function () {
				dependencyMounter._mountOnObject(mountee, dependencyNames);
				expect(mountee.$http).toBe($http);
			});

		});

		describe('_extendObject', function () {
			var dependencyNames;
			var mountee;

			beforeEach(function () {
				spyOn($injector, 'get').and.callThrough();
				spyOn(angular, 'extend').and.callThrough();

				dependencyNames = ['$http'];
				mountee = {};
			});

			it('should invoke $injector.get with the dependency name', function () {
				dependencyMounter._extendObject(mountee, dependencyNames);
				expect($injector.get).toHaveBeenCalledWith(dependencyNames[0]);
			});

			it('should invoke angular.extend with mountee and dependecy ', function () {
				dependencyMounter._extendObject(mountee, dependencyNames);
				expect(angular.extend).toHaveBeenCalledWith(mountee, $http);
			});
		});

	});

	describe('public methods', function () {

		var Constructor, instance, dependecyArray;

		beforeEach(function () {
			spyOn(dependencyMounter, '_validateConstructor');
			spyOn(dependencyMounter, '_validateInstance');
			spyOn(dependencyMounter, '_validateArrDependencies');
			spyOn(dependencyMounter, '_mountOnObject');
			spyOn(dependencyMounter, '_extendObject');

			Constructor = angular.noop;
			instance = {};
			dependecyArray = [];

		});

		describe('mountOnConstructor', function () {
			beforeEach(function () {
				dependencyMounter.mountOnConstructor(Constructor, dependecyArray);
			});

			it('should invoke _validateConstructor with "mountOnConstructor" and Constructor', function () {
				expect(dependencyMounter._validateConstructor)
					.toHaveBeenCalledWith('mountOnConstructor', Constructor);
			});

			it('should invoke _validateArrDependencies with "mountOnConstructor" and arrDependencies', function () {
				expect(dependencyMounter._validateArrDependencies)
					.toHaveBeenCalledWith('mountOnConstructor', dependecyArray);
			});

			it('should invoke _mountOnObject with constructor.prototype and arrDependencies', function () {
				expect(dependencyMounter._mountOnObject)
					.toHaveBeenCalledWith(Constructor.prototype, dependecyArray);
			});
		});

		describe('mountOnInstance', function () {
			beforeEach(function () {
				dependencyMounter.mountOnInstance(instance, dependecyArray);
			});

			it('should invoke _validateInstance with "mountOnInstance" and instance', function () {
				expect(dependencyMounter._validateInstance)
					.toHaveBeenCalledWith('mountOnInstance', instance);
			});

			it('should invoke _validateArrDependencies with "mountOnInstance" and arrDependencies', function () {
				expect(dependencyMounter._validateArrDependencies)
					.toHaveBeenCalledWith('mountOnInstance', dependecyArray);
			});

			it('should invoke _mountOnObject with instance and arrDependencies', function () {
				expect(dependencyMounter._mountOnObject)
					.toHaveBeenCalledWith(instance, dependecyArray);
			});
		});

		describe('extendConstructor', function () {
			beforeEach(function () {
				dependencyMounter.extendConstructor(Constructor, dependecyArray);
			});

			it('should invoke _validateConstructor with "mountOnConstructor" and Constructor', function () {
				expect(dependencyMounter._validateConstructor)
					.toHaveBeenCalledWith('mountOnConstructor', Constructor);
			});

			it('should invoke _validateArrDependencies with "mountOnConstructor" and arrDependencies', function () {
				expect(dependencyMounter._validateArrDependencies)
					.toHaveBeenCalledWith('mountOnConstructor', dependecyArray);
			});

			it('should invoke _mountOnObject with constructor.prototype and arrDependencies', function () {
				expect(dependencyMounter._extendObject)
					.toHaveBeenCalledWith(Constructor.prototype, dependecyArray);
			});
		});

		describe('extendInstance', function () {
			beforeEach(function () {
				dependencyMounter.extendInstance(instance, dependecyArray);
			});

			it('should invoke _validateInstance with "mountOnInstance" and instance', function () {
				expect(dependencyMounter._validateInstance)
					.toHaveBeenCalledWith('mountOnInstance', instance);
			});

			it('should invoke _validateArrDependencies with "mountOnInstance" and arrDependencies', function () {
				expect(dependencyMounter._validateArrDependencies)
					.toHaveBeenCalledWith('mountOnInstance', dependecyArray);
			});

			it('should invoke _mountOnObject with instance and arrDependencies', function () {
				expect(dependencyMounter._extendObject)
					.toHaveBeenCalledWith(instance, dependecyArray);
			});
		});
	});
});
