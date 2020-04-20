(function () {
	'use strict';

	var globalLogic = {
		utils: null,
		testStrings: function (strings, utility, ctrl){
			var testOptions = ["shouldPass", "shouldFail"];

			var variables = {
				params: {
					myParam: null
				},
				data: {
					myData: null
				}
			};

			testOptions.forEach(function (testOption) {
				for (var i = 0; i < strings[testOption].length; i++){
					var additionalVarables = "";
					var test = strings[testOption][i];

					// if the input is array we suppose to concat additional variables
					if (angular.isArray( test.input )) {
						variables.params.myParam = test.input[0];
						variables.data.myData = test.input[0];
						test.input = test.input.splice(1, test.input.length - 1);
						additionalVarables = ":" + test.input.join(":");
					} else {
						variables.params.myParam = test.input;
						variables.data.myData = test.input;
					}

					var paramsStr = "{{@myParam:" + utility + additionalVarables + "}}";
					var paramOutCome = globalLogic.utils.strings.parseValue(paramsStr, variables.data, variables.params);

					var dataStr = "{{myData:" + utility  + additionalVarables + "}}";
					var dataOutCome = globalLogic.utils.strings.parseValue(dataStr, variables.data, variables.params);


					switch (testOption) {
						case "shouldPass":
							expect(dataOutCome).toBe(test.output);
							expect(paramOutCome).toBe(test.output);
							break;
						case "shouldFail":
							expect(dataOutCome).not.toBe(test.output);
							expect(paramOutCome).not.toBe(test.output);
							break;
					}

				}
			});
		}
	};

	describe('utils.service.legacy: All string functions', function() {
		// Load the module with MainController
		beforeEach(module('Fortscale'));

		var ctrl, scope;
		// inject the $controller and $rootScope services
		// in the beforeEach block
		beforeEach(inject(function($controller, $rootScope) {
			// Create a new scope that's a child of the $rootScope
			scope = $rootScope.$new();
			// Create the controller
			ctrl = $controller('MainController', {
				$scope: scope
			});
		}));

		beforeEach(inject(function (utils) {
			ctrl.utils = utils;
			globalLogic.utils = utils;
		}));

		it('should absolute every value and return it as a string, if it can not be parsed to a number it should return the string',
			function () {
				var utility = "abs";
				var strings = {
					shouldPass:[
						{ input:"0", output:"0"},
						{ input:null, output:""},
						{ input:-5, output:"5"},
						{ input:"15", output:"15"},
						{ input:"", output:""},
						{ input:"aaa bbb", output:"aaa bbb"}
					],
					shouldFail:[
						{ input:"-2", output:"2"},
						{ input:-2, output:"-2"}
					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('transform a number of bytes to a readable value',
			function() {
				var utility = "bytesCount";
				var strings = {
					shouldPass: [
						{ input: 1050, output: "1.05 KB"},
						{ input: 15888, output: "15.89 KB"},
						{ input: 1321555, output: "1.32 MB"},
						{ input: 10000000000, output: "10.00 GB"}
					],
					shouldFail: [

					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should capitalize every first letter of a word in a string',
			function() {
				var utility = "capitalize";
				var strings = {
					shouldPass: [
						{ input: "this is My", output: "This Is My"},
						{ input: null, output: ""},
						{ input: "abc", output: "Abc"},
						{ input: "", output: ""},
					],
					shouldFail: [

					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should return a timestamp in several forms',
			function() {
				var utility = "date";
				var strings = {
					shouldPass: [
						{
							input: ["11155654648486","unixtimestamp","","start"],
							output:"11155622400"
						},
						{
							input: ["11155654648486",null,"","start"],
							output:"Jul 06 2323, 00:00:00"
						},
						{
							input: ["11155654648486",null,"","end"],
							output:"Jul 06 2323, 23:59:59"
						}
						/*{
						 input: ["Jul 15 2323, 23:59:59",null,"100","start"],
						 output:"Jul 15 2323, 00:00:00"
						 }*/
					],
					shouldFail: [

					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should turn a decimal value to persentage',
			function() {
				var utility = "decimalToPercentage";
				var strings = {
					shouldPass: [
						{ input: 0.5, output: "50.00"},
						{ input: 0.253, output: "25.30"},
						{ input: null, output: ""},
						{ input: "abc", output: "abc"},
						{ input: "", output: ""},
					],
					shouldFail: [

					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should return top / regular table name according to score',
			function() {
				var utility = "eventsTable";
				var strings = {
					shouldPass: [
						{ input: [50,"authenticationscores"], output: "authenticationscores_top"},
						{ input: [49,"sshscores"], output: "sshscores"},
						// because the requested table is already with _top
						{ input: [10,"authenticationscores_top"], output: "authenticationscores_top"}
					],
					shouldFail: [
						{ input: [51,"sshscores"], output: "sshscores"},
					]
				};
				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should replace a string separated with a char with a different expression',
			function() {
				var utility = "join";
				var strings = {
					shouldPass: [
						{ input: ["shavit,yossi,moshe","=true&",","], output: "shavit=true&yossi=true&moshe"},
						{ input: ["shavit,yossi,moshe","=true&"], output: "shavit=true&yossi=true&moshe"},
						{ input: ["a,b,c","-"], output: "a-b-c"}

					],
					shouldFail: [

					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should multiply a number with a value',
			function() {
				var utility = "multiply";
				var strings = {
					shouldPass: [
						{ input: ["5","5"], output: "5"},
						{ input: ["a",5], output: "a"},
						{ input: [5,5], output: "25"},
						{ input: [5,"5"], output: "25"}


					],
					shouldFail: [

					]
				};

				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should return the not null value between two variables',
			function() {
				var utility = "or";
				var strings = {
					shouldPass: [
						{ input: [null,"shavit"], output: "shavit"},
						{ input: ["zohan",null], output: "zohan"},
						{ input: ["var1","var2"], output: "var1"},
					],
					shouldFail: [

					]
				};
				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should return a string with the selected length and the selected chars to the left',
			function() {
				var utility = "padLeft";
				var strings = {
					shouldPass: [
						{ input: ["shavit", 10,"-"], output: "----shavit"},
						{ input: [222, 10,"-"], output: "-------222"},
						{ input: [6, 2,"0"], output: "06"}

					],
					shouldFail: [

					]
				};
				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should remove the domain (all chars after the @ char)',
			function() {
				var utility = "removeAtDomain";
				var strings = {
					shouldPass: [
						{ input: "myName@fortscale.dom", output: "myName"},
						{ input: null, output: ""}
					],
					shouldFail: [

					]
				};
				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should check that the domain is not an ip address and then remove all chars after the first . char',
			function() {
				var utility = "removeDotDomain";
				var strings = {
					shouldPass: [
						{ input: "myName.123.554.336.848", output: "myName"},
						{ input: "123.554.336.848", output: "123.554.336.848"}, // it is a n ip address so it is OK
						{ input: null, output: ""}
					],
					shouldFail: [

					]
				};
				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should convert seconds to hours',
			function() {
				var utility = "secondsToHour";
				var strings = {
					shouldPass: [
						{ input: "5824", output: "01:37:04"},
						{ input: 5824, output: "01:37:04"}, // it is a n ip address so it is OK
						{ input: null, output: "00:00:00"}
					],
					shouldFail: [

					]
				};
				globalLogic.testStrings(strings,utility,ctrl);
			});

		it('should parse duration to time format', function () {
			var utility = "diffToPrettyTime";
			var strings = {
				shouldPass: [
					{input: [18000, "seconds"], output: "05:00:00"},
					{input: [88500, "seconds"], output: "1d"},
					{input: [88500 + 18000, "seconds"], output: "1d"},
					{input: [172801, "seconds"], output: "2d"}
				],
				shouldFail: []
			};
			globalLogic.testStrings(strings, utility, ctrl);
		});

	});

	describe('utils.service', function () {

		// utils is the service that is to be tested
		var utils;

		// Instantiate the module
		beforeEach(module('Utils'));

		// Inject the 'utils' factory and set it to a variable.
		// All the subsequent methods will have the factory in their scope (via closure)
		beforeEach(inject(function ($injector) {
			utils = $injector.get('utils');
		}));

		it('should be defined', function () {
			expect(utils).toBeDefined();
		});


		describe('_parseStringFormatters', function () {

			describe('abs', function () {
				var val;
				var mockIsNumber;
				var originalMathAbs;
				var mockMathAbs;

				beforeEach(function () {
					mockIsNumber = spyOn(angular, 'isNumber');
					originalMathAbs = Math.abs;
					mockMathAbs = spyOn(Math, 'abs');
					val = 'something';
				});

				afterEach(function () {
					Math.abs = originalMathAbs;
				});

				it('should invoke angular.isNumber with value argument', function () {
					utils._parseStringFormatters.abs(val);
					expect(angular.isNumber).toHaveBeenCalledWith(val);
				});

				it('should invoke Math.abs with value argument when angular.isNumber returns true', function () {
					mockIsNumber.and.returnValue(true);
					utils._parseStringFormatters.abs(val);
					expect(Math.abs).toHaveBeenCalledWith(val);
				});

				it('should not invoke Math.abs when angular.isNumber returns false', function () {
					mockIsNumber.and.returnValue(false);
					utils._parseStringFormatters.abs(val);
					expect(Math.abs).not.toHaveBeenCalled();
				});

				it('should return Math.abs() when angular.isNumber returns true', function () {
					var mathAbsReturn = {};
					mockIsNumber.and.returnValue(true);
					mockMathAbs.and.returnValue(mathAbsReturn);

					expect(utils._parseStringFormatters.abs(val)).toBe(mathAbsReturn);
				});

				it('should return the original value when angular.isNumber returns false', function () {
					mockIsNumber.and.returnValue(false);
					expect(utils._parseStringFormatters.abs(val)).toBe(val);
				});
			});

			describe('bytesCount', function () {
				var val, utilsNumbersBytesCountReturn = {};

				beforeEach(function () {
					spyOn(utils.numbers, 'bytesCount').and.returnValue(utilsNumbersBytesCountReturn);
					val = {};
				});

				it('should invoke utils.numbers.bytesCounts with value argument', function () {
					utils._parseStringFormatters.bytesCount(val);
					expect(utils.numbers.bytesCount).toHaveBeenCalledWith(val);
				});

				it('should return utils.numbers.bytesCounts(val) ', function () {
					expect(utils._parseStringFormatters.bytesCount(val)).toBe(utilsNumbersBytesCountReturn);
				});
			});


			describe('capitalize', function () {

				beforeEach(function () {
					spyOn(utils.strings, 'capitalize');
				});

				it('should invoke utils.strings.capitalize with value argument', function () {

					var val = "Hello";
					utils._parseStringFormatters.capitalize(val);
					expect(utils.strings.capitalize).toHaveBeenCalledWith(val);

				});

				it('should utils._parseStringFormatters.capitalize(val) have the same result as utils.strings.capitalize(val)', function () {

					var val = "Hello";
					var actual = utils._parseStringFormatters.capitalize(val);

					var expected = utils.strings.capitalize(val);
					expect(actual, expected);

				});
			});

			describe('decimalToPercentage', function () {
				var mockIsNumber;
				var val;

				beforeEach(function () {
					mockIsNumber = spyOn(angular, 'isNumber').and.returnValue(true);
					val = 100;
				});

				it('should invoke angular.isNumber with value', function () {
					utils._parseStringFormatters.decimalToPercentage(val);
					expect(angular.isNumber).toHaveBeenCalledWith(val);
				});

				it('should return the value if angular.isNumber returns false', function () {
					mockIsNumber.and.returnValue(false);
					expect(utils._parseStringFormatters.decimalToPercentage(val)).toBe(val);
				});

				it('should return (n * 100).toFixed(2)', function () {
					val = 0.55;
					expect(utils._parseStringFormatters.decimalToPercentage(val)).toBe('55.00');
					val = 0.5555;
					expect(utils._parseStringFormatters.decimalToPercentage(val)).toBe('55.55');
					val = 0.555555;
					expect(utils._parseStringFormatters.decimalToPercentage(val)).toBe('55.56');
				});
			});

			describe('eventsTable', function () {
				var getEventsTableNameReturnValue;
				var mockGetEventsTableName;
				var score, tableName;

				beforeEach(function () {
					getEventsTableNameReturnValue = 'something';
					mockGetEventsTableName = spyOn(utils.strings, 'getEventsTableName').and.returnValue(getEventsTableNameReturnValue);
					score = 'score';
					tableName = 'tableName';
				});

				it('should invoke methods.strings.getEventsTableName with arguments tableName and score', function () {
					utils._parseStringFormatters.eventsTable(score, tableName);
					expect(utils.strings.getEventsTableName).toHaveBeenCalledWith(tableName, score);
				});

				it('should return the result of methods.strings.getEventsTableName(tableName, score)', function () {
					expect(utils._parseStringFormatters.eventsTable(score, tableName)).toBe(getEventsTableNameReturnValue);
				});
			});
		});

		describe('arrays', function () {
			describe('areEqual', function () {
				it('should throw if first argument or second argument is missing', function () {
					// No first argument - should throw
					expect(utils.arrays.areEqual.bind(utils, undefined, [])).toThrowError("Missing values to compare arrays.");
					// No second argument - should throw
					expect(utils.arrays.areEqual.bind(utils, [], undefined)).toThrowError("Missing values to compare arrays.");
					// No arguments - should throw
					expect(utils.arrays.areEqual.bind(utils)).toThrowError("Missing values to compare arrays.");

				});


				it('should invoke angular.isArray with first argument and second argument', function () {
					var arg1 = {obj1: 1}, arg2 = {obj2: 2};
					spyOn(angular, 'isArray').and.returnValue(true);

					utils.arrays.areEqual(arg1, arg2);
					expect(angular.isArray.calls.allArgs()[0][0]).toBe(arg1);
					expect(angular.isArray.calls.allArgs()[1][0]).toBe(arg2);
				});

				it('should throw if isArray returns false for first or second argument', function () {
					var arg1 = {obj1: 1}, arg2 = {obj2: 2};
					spyOn(angular, 'isArray').and.returnValue(false);

					expect(utils.arrays.areEqual.bind(utils, arg1, arg2)).toThrowError("areEqual received non-array parameter(s).");
				});

				it('should not throw if isArray returns true for first and second argument', function () {
					var arg1 = {obj1: 1}, arg2 = {obj2: 2};
					spyOn(angular, 'isArray').and.returnValue(true);

					expect(utils.arrays.areEqual.bind(utils, arg1, arg2)).not.toThrowError("areEqual received non-array parameter(s).");
				});

				it('should return false if arrays are not of the same length', function () {
					spyOn(angular, 'isArray').and.returnValue(true);
					var arg1 = [1, 2, 3], arg2 = [4, 5, 6, 7];
					expect(utils.arrays.areEqual(arg1, arg2)).toBe(false);
				});

				it('should invoke utils.objects.areEqual with arg1[i] and arg2[i] for each member of the first array', function () {
					spyOn(angular, 'isArray').and.returnValue(true);
					spyOn(utils.objects, 'areEqual').and.returnValue(true);
					var arg1 = [1, 2, 3], arg2 = [1, 2, 3];
					utils.arrays.areEqual(arg1, arg2);
					expect(utils.objects.areEqual.calls.count()).toBe(3);
					expect(utils.objects.areEqual.calls.allArgs()[0][0]).toBe(arg1[0]);
					expect(utils.objects.areEqual.calls.allArgs()[0][1]).toBe(arg2[0]);
					expect(utils.objects.areEqual.calls.allArgs()[1][0]).toBe(arg1[1]);
					expect(utils.objects.areEqual.calls.allArgs()[1][1]).toBe(arg2[1]);
					expect(utils.objects.areEqual.calls.allArgs()[2][0]).toBe(arg1[2]);
					expect(utils.objects.areEqual.calls.allArgs()[2][1]).toBe(arg2[2]);
				});

				it('should return false if utils.objects.areEqual returns false', function () {
					spyOn(angular, 'isArray').and.returnValue(true);
					spyOn(utils.objects, 'areEqual').and.returnValue(false);
					var arg1 = [1, 2, 3], arg2 = [1, 2, 3];
					expect(utils.arrays.areEqual(arg1, arg2)).toBe(false);
				});

				it('should return true if arrays are of same length and utils.objects.areEqual returns true', function () {
					spyOn(angular, 'isArray').and.returnValue(true);
					spyOn(utils.objects, 'areEqual').and.returnValue(true);
					var arg1 = [1, 2, 3], arg2 = [1, 2, 3];
					expect(utils.arrays.areEqual(arg1, arg2)).toBe(true);
				});
			});

			describe('doesNotContain', function () {
				describe('validations', function () {
					var arg1, arg2;
					var _errMsg;

					beforeEach(function () {
						_errMsg = 'utils.arrays.doesNotContain: ';
						arg1 = [];
						arg2 = [];
					});

					it('should invoke angular.isArray with first argument then with second argument', function () {
						spyOn(angular, 'isArray').and.returnValue(true);
						utils.arrays.doesNotContain(arg1, arg2);
						expect(angular.isArray.calls.allArgs()[0][0]).toBe(arg1);
						expect(angular.isArray.calls.allArgs()[1][0]).toBe(arg2);
					});

					it('should throw TypeError if arg1 is not an array', function () {
						// Replace angular.isArray with fake so it returns false for first argument
						function fakeIsArray(arg) {
							return !(arg === arg1);
						}

						spyOn(angular, 'isArray').and.callFake(fakeIsArray);
						expect(utils.arrays.doesNotContain.bind(utils, arg1, arg2)).toThrowError(TypeError);
						expect(utils.arrays.doesNotContain.bind(utils, arg1, arg2)).toThrowError(_errMsg + 'first argument must be an array.');
					});

					it('should throw TypeError if arg2 is not an array', function () {
						// Replace angular.isArray with fake so it returns false for second argument
						function fakeIsArray(arg) {
							return !(arg === arg2);
						}

						spyOn(angular, 'isArray').and.callFake(fakeIsArray);
						expect(utils.arrays.doesNotContain.bind(utils, arg1, arg2)).toThrowError(TypeError);
						expect(utils.arrays.doesNotContain.bind(utils, arg1, arg2)).toThrowError(_errMsg + 'second argument must be an array.');
					});
				});
				describe('functionality', function () {
					beforeEach(function () {
						spyOn(angular, 'isArray').and.returnValue(true);
					});

					it('should return false if first array contains members from second array', function () {
						var arr1, arr2;
						// Test on member
						arr1 = [1, 2];
						arr2 = [1];
						expect(utils.arrays.doesNotContain(arr1, arr2)).toBe(false);
						// Test two members
						arr1 = [1, 2];
						arr2 = [1, 2];
						expect(utils.arrays.doesNotContain(arr1, arr2)).toBe(false);
						// Test members that are not numbers
						arr1 = [1, 2, Array];
						arr2 = [Array];
						expect(utils.arrays.doesNotContain(arr1, arr2)).toBe(false);
					});
					it('should return true if the second array has no members that are also in the first array', function () {
						var arr1 = [1, 2, 3], arr2 = [4, 5, 6];
						expect(utils.arrays.doesNotContain(arr1, arr2)).toBe(true);
					});
				});

			});

			describe('find', function () {
				var isArrayMock, isFunctionMock;
				var arg1, arg2;
				var _errMsg;

				beforeEach(function () {
					isArrayMock = spyOn(angular, 'isArray');
					isFunctionMock = spyOn(angular, 'isFunction');
					arg1 = [];
					arg2 = angular.noop;
					_errMsg = 'utils.arrays.find: ';
				});

				describe('validations', function () {

					it('should invoke angular.isArray with the first argument', function () {

						isArrayMock.and.returnValue(true);
						isFunctionMock.and.returnValue(true);
						utils.arrays.find(arg1, arg2);

						expect(angular.isArray).toHaveBeenCalledWith(arg1);
					});

					it('should invoke angular.isFunction with the second argument', function () {

						isArrayMock.and.returnValue(true);
						isFunctionMock.and.returnValue(true);
						utils.arrays.find(arg1, arg2);

						expect(angular.isFunction).toHaveBeenCalledWith(arg2);
					});

					it('should throw TypeError if first argument is not an array', function () {
						isArrayMock.and.returnValue(false);
						expect(utils.arrays.find.bind(utils, arg1, arg2)).toThrowError(TypeError);
						expect(utils.arrays.find.bind(utils, arg1, arg2)).toThrowError(_errMsg + 'arr argument must be an array.');
					});

					it('should throw TypeError if second argument is not an array', function () {
						isArrayMock.and.returnValue(true);
						isFunctionMock.and.returnValue(false);
						expect(utils.arrays.find.bind(utils, arg1, arg2)).toThrowError(TypeError);
						expect(utils.arrays.find.bind(utils, arg1, arg2)).toThrowError(_errMsg + 'findFunction argument must be a function.');
					});
				});
				describe('functionality', function () {
					beforeEach(function () {
						// Disables validations
						isArrayMock.and.returnValue(true);
						isFunctionMock.and.returnValue(true);
					});

					it('should invoke findFunction argument for each member of the array', function () {
						arg1 = [1, 2, 3];
						arg2 = jasmine.createSpy('findFunction');
						utils.arrays.find(arg1, arg2);
						expect(arg2.calls.count()).toBe(3);
					});

					it('should invoke findFunction with each member in the array', function () {
						arg1 = [1, 2, 3];
						arg2 = jasmine.createSpy('findFunction');
						utils.arrays.find(arg1, arg2);
						expect(arg2).toHaveBeenCalledWith(1);
						expect(arg2).toHaveBeenCalledWith(2);
						expect(arg2).toHaveBeenCalledWith(3);
					});

					it('should return the first member that the invocation of findFunction with that member returns true', function () {
						var obj1 = {obj: 1}, obj2 = {obj: 2}, obj3 = {obj: 3};
						var arr = [obj1, obj2, obj3];

						function returnTrueWhenObj2(val) {
							return val === obj2;
						}

						expect(utils.arrays.find(arr, returnTrueWhenObj2)).toBe(obj2);
					});

					it('should return null when no iteration invocation of findFunction returns true', function () {
						var arr = [1, 2, 3, 4];

						function findfunctionThatReturnsFalse() {
							return false
						}

						expect(utils.arrays.find(arr, findfunctionThatReturnsFalse)).toBe(null);
					});
				});
			});
		});

		describe('date', function () {

			it('should be defined', function () {
				expect(utils.date).toBeDefined();
			});

			describe('isTimeStamp', function () {

				it('should not throw if argument is string', function () {
					function shouldNotThrow() {
						utils.date.isTimeStamp('is string');
					}

					expect(shouldNotThrow).not.toThrow();
				});

				it('should not throw if argument is number', function () {
					function shouldNotThrow() {
						utils.date.isTimeStamp(100);
					}

					expect(shouldNotThrow).not.toThrow();
				});

				it('should throw TypeError if argument is not a string or number', function () {
					expect(utils.date.isTimeStamp.bind(utils, {})).toThrowError(TypeError);
				});

				it('should return true if argument is a 10 or 13 digits number or string', function () {
					expect(utils.date.isTimeStamp(1234567890)).toBe(true);
					expect(utils.date.isTimeStamp('1234567890')).toBe(true);
					expect(utils.date.isTimeStamp(1234567890123)).toBe(true);
					expect(utils.date.isTimeStamp('1234567890123')).toBe(true);
				});

				it('should return false if any of the chars is not a digit', function () {
					expect(utils.date.isTimeStamp('123456789A')).toBe(false);
				});

				it('should return false if the string\'s or number\'s length is not 10 or 13', function () {

					expect(utils.date.isTimeStamp('123456789')).toBe(false);
					expect(utils.date.isTimeStamp('12345678901')).toBe(false);
					expect(utils.date.isTimeStamp(123456789)).toBe(false);
				});
			});


		});

		describe('numbers', function () {
			describe('bytesCount', function () {
				var val;
				var mockParseInt;
				var originalParseInt;

				beforeEach(function () {
					originalParseInt = window.parseInt;
					mockParseInt = spyOn(window, 'parseInt').and.callThrough();
				});

				afterEach(function () {
					window.parseInt = originalParseInt;
				});

				it('should return value if value is falsey', function () {
					val = undefined;
					expect(utils.numbers.bytesCount(val)).toBe(val);
				});

				it('should invoke parseInt with the value argument and the redix 10', function () {
					val = '100';
					utils.numbers.bytesCount(val);
					expect(window.parseInt).toHaveBeenCalledWith(val, 10);
				});

				it('should throw RangeError when value can not be parsed and NaN is returned', function () {
					val = '100';
					mockParseInt.and.returnValue(NaN);

					function testFunc() {
						utils.numbers.bytesCount(val);
					}

					expect(testFunc).toThrowError(RangeError);
					expect(testFunc).toThrowError('utils.numbers.bytesCount: parseInt(value, 10) returned NaN.');
				});

				it('should return n + " B" if n is less then 10^3', function () {
					val = 900;
					expect(utils.numbers.bytesCount(val)).toBe('900 B');
					val = '700';
					expect(utils.numbers.bytesCount(val)).toBe('700 B');
				});

				it('should return ((n / 10^3) with two digits) + " KB" if n is less then 10^6', function () {
					val = 1010;
					expect(utils.numbers.bytesCount(val)).toBe('1.01 KB');
					val = '999990';
					expect(utils.numbers.bytesCount(val)).toBe('999.99 KB');
				});

				it('should return ((n / 10^6) with two digits) + " MB" if n is less then 10^9', function () {
					val = 1010000;
					expect(utils.numbers.bytesCount(val)).toBe('1.01 MB');
					val = '999990000';
					expect(utils.numbers.bytesCount(val)).toBe('999.99 MB');
				});

				it('should return ((n / 10^6) with two digits) + " GB" if n is less then 10^9', function () {
					val = 1010000000;
					expect(utils.numbers.bytesCount(val)).toBe('1.01 GB');
					val = '999990000000';
					expect(utils.numbers.bytesCount(val)).toBe('999.99 GB');
				});

				it('should return ((n / 10^9) with two digits) + " TB" if n is less then 10^12', function () {
					val = 1010000000000;
					expect(utils.numbers.bytesCount(val)).toBe('1.01 TB');
					val = '999990000000000';
					expect(utils.numbers.bytesCount(val)).toBe('999.99 TB');
				});
			});
		});

		describe('strings', function () {
			describe('getEventsTableName', function () {
				var errMsg;
				var tableNameWhiteList;
				var tableName, minScore;
				var mockIsNumber;

				beforeEach(function () {
					errMsg = 'utils.strings.getEventsTableName: ';
					tableNameWhiteList = ["authenticationscores", "sshscores", "vpndatares", "vpnsessiondatares"];
					mockIsNumber = spyOn(angular, 'isNumber').and.returnValue(true);
				});

				it('should throw ReferenceError if tableName is falsy', function () {
					tableName = undefined;
					function testFunc() {
						utils.strings.getEventsTableName(tableName, minScore);
					}

					expect(testFunc).toThrowError(ReferenceError);
					expect(testFunc).toThrowError(errMsg + 'tableName argument must not be falsy.');

				});

				it('should check against tableName whiteList and return the table as-is if not in whiteList', function () {
					tableName = 'notInWhiteList';
					expect(utils.strings.getEventsTableName(tableName, minScore)).toBe(tableName);
				});

				it('should invoke angular.isNumber with minScore if minScore is not falsy', function () {
					tableName = 'authenticationscores';
					minScore = 75;

					utils.strings.getEventsTableName(tableName, minScore);

					expect(angular.isNumber).toHaveBeenCalledWith(minScore);
				});

				it('should not invoke angular.isNumber with minScore if minScore is falsy', function () {
					tableName = 'someName';
					minScore = 0;

					utils.strings.getEventsTableName(tableName, minScore);
					expect(angular.isNumber).not.toHaveBeenCalled();
				});

				it('should return the tableName as-is if angular.isNumber returns false', function () {
					mockIsNumber.and.returnValue(false);
					tableName = 'someName';
					minScore = 75;
					expect(utils.strings.getEventsTableName(tableName, minScore)).toBe(tableName);
				});

				it('should return the tableName as-is if minScore is falsy', function () {
					tableName = 'authenticationscores';
					minScore = 0;
					expect(utils.strings.getEventsTableName(tableName, minScore)).toBe(tableName);
				});

				it('should return the tableName as-is if minScore is less then 50', function () {
					tableName = 'authenticationscores';
					minScore = 20;
					expect(utils.strings.getEventsTableName(tableName, minScore)).toBe(tableName);
				});

				it('should return tableName + \'_top\' if minScore is equal or greater then 50 and in whiteList', function () {
					tableName = 'authenticationscores';
					minScore = 75;
					expect(utils.strings.getEventsTableName(tableName, minScore)).toBe(tableName + '_top');
				});

			});

			describe('capitalize', function () {

				it('should return value if value is falsey', function () {
					var val = undefined;
					expect(utils.strings.capitalize(val)).toBe(val);
				});

				it('should capitalize "hello" to Hello', function () {
					var val = "hello";
					var expected = "Hello";
					expect(utils.strings.capitalize(val)).toBe(expected);
				});

				it('should capitalize  HELLO to Hello', function () {
					var val = "HELLO";
					var expected = "Hello";
					expect(utils.strings.capitalize(val)).toBe(expected);
				});

				it('should capitalize  "HELLO world" to "Hello World"', function () {
					var val = "HELLO world";
					var expected = "Hello World";
					expect(utils.strings.capitalize(val)).toBe(expected);
				});


				it('should capitalize  different combination of lower and upper case and alpha numberic text (include digits)', function () {
					var val = "very very VERY vEry very very VERY vEry " +
						"lOnG String 112343556667";
					var expected = "Very Very Very Very Very Very Very Very " +
						"Long String 112343556667";
					expect(utils.strings.capitalize(val)).toBe(expected);

				});

				it('should capitalize  string of number', function () {
					var expected = "112343556667";
					var val = "112343556667";
					expect(utils.strings.capitalize(val)).toBe(expected);

				});

				it('should throw execption when capitalize a number', function () {

					var val = 112343556667;

					var actual = function () {
						utils.strings.capitalize(val)
					};

					expect(utils.strings.capitalize.bind(utils, val)).toThrowError(TypeError);
					expect(utils.strings.capitalize.bind(utils, val)).toThrowError('utils.strings.capitalize: str is not a String');
				});

				it('should throw execption when capitalize an object', function () {

					var val = {a: 'test'};
					var actual = function () {
						utils.strings.capitalize(val)
					};

					expect(utils.strings.capitalize.bind(utils, val)).toThrowError(TypeError);
					expect(utils.strings.capitalize.bind(utils, val)).toThrowError('utils.strings.capitalize: str is not a String');
				});


			});
		});
	});
}());
