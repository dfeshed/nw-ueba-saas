/**
 * @file The transition map for liquid-fire.
 * Each call to this.transition() below establishes a mapping from a route transition criteria
 * (e.g., fromRoute(..) and/or toRoute(..)) to a transition animation (e.g., this.use(transitionName)).
 */

export default function(){

    // For now, all transitions will use crossFade.
    this.transition(
        this.use("crossFade")
    );
}
