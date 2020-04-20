import { run } from '@ember/runloop';

export default function destroyApp(application) {
  localStorage.clear();
  run(application, 'destroy');
}
