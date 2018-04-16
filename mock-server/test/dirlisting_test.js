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
      .get('/locales/')
      .expect('Content-Type', /json/)
      .end(function(err, res) {
        if (err) {
          throw err;
        }

        expect(res.body.length).to.eql(3);
        expect(res.body[0]).to.eql({ 'name': 'german_de-de.js', 'size': 15 });
        expect(res.body[1]).to.eql({ 'name': 'japanese_ja-jp.js', 'size': 17 });
        expect(res.body[2]).to.eql({ 'name': 'spanish_es-mx.js', 'size': 16 });
        done();
      });
  });

  it('will return static file from locales directory', (done) => {
    request(server)
      .get('/locales/spanish_es-mx.js')
      .expect('Content-Type', /text\/javascript/)
      .expect('Content-Length', '117279')
      .end(function(err, res) {
        if (err) {
          throw err;
        }

        expect(res.status).to.eql(200);
        done();
      });
  });

  it('will return 404 when static file not found in locales directory', (done) => {
    request(server)
      .get('/locales/foo.bar')
      .expect('Content-Type', /text\/html/)
      .end(function(err, res) {
        if (err) {
          throw err;
        }

        expect(res.status).to.eql(404);
        done();
      });
  });

});
