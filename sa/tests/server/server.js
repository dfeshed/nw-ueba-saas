/* eslint-disable */

var reconMockDirectory = require('../../../recon').mockDestinations;

require('mock-server').startServer([__dirname, reconMockDirectory]);