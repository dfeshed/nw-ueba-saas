import expect from 'expect.js';
import request from 'supertest';

import { startServer } from './util';

describe('mock server routes', () => {

  let server;

  before((done) => {
    startServer((_server) => {
      server = _server;
      done();
    });
  });

  after(() => server.close());

  it('will respond when configured with JSON response', (done) => {

    request(server)
      .get('/foo/bar')
      .expect('Content-Type', /json/)
      .end(function(err, res) {
        if (err) {
          throw err;
        }

        expect(res.body.this).to.eql('is');
        expect(res.body.json).to.eql('return');
        done();
      });
  });

  it('will respond when configured with express route response', (done) => {
    request(server)
      .get('/baz/what?passed=hello')
      .expect('Content-Type', /json/)
      .end(function(err, res) {
        if (err) {
          throw err;
        }

        expect(res.body.something).to.eql('else');
        expect(res.body.passed).to.eql('hello');
        done();
      });
  });

});

