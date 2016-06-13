/**
 * @public
 * @description Instantiates a Router, necessary on integration tests when the Component has a dependency with the router.
 * eg: link-to helper is used in the component's template
 * @param container
 */
export default function({ container }) {
  const router = container.lookup('router:main');
  router.startRouting(true);
}