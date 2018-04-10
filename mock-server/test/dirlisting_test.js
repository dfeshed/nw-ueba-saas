import expect from 'expect.js';
import request from 'supertest';

import { startServer } from './util';

describe('directory listing', () => {

  let server;

  before((done) => {
    startServer((_server) => {
      server = _server;
      done();
    });
  });

  after(() => server.close());

  it('will return directory listing for locales directory', (done) => {
    request(server)
      .get('/locales')
      .expect('Content-Type', /json/)
      .end(function(err, res) {
        if (err) {
          throw err;
        }

        expect(res.body.length).to.eql(2);
        expect(res.body[0]).to.eql({ 'name': 'german_de-de.js', 'size': 15 });
        expect(res.body[1]).to.eql({ 'name': 'spanish_es-mx.js', 'size': 16 });
        done();
      });
  });

});
