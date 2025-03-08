# Si-frame

[![Clojars Project](https://img.shields.io/clojars/v/fi.metosin/si-frame.svg)](https://clojars.org/fi.metosin/si-frame)
[![Slack](https://img.shields.io/badge/slack-signaali-orange.svg?logo=slack)](https://clojurians.slack.com/app_redirect?channel=signaali)
[![cljdoc badge](https://cljdoc.org/badge/fi.metosin/si-frame)](https://cljdoc.org/d/fi.metosin/si-frame)

This project is a fork of [Re-frame `v1.4.3`](https://github.com/day8/re-frame/tree/v1.4.3) where
the reactivity is implemented using [Signaali](https://github.com/metosin/signaali)
via [Siagent](https://github.com/metosin/siagent) instead of [Reagent](https://github.com/reagent-project/reagent).

It was created:
- to battle-proof Signaali's API before its release,
- to demonstrate how Signaali could be used in future versions of Re-frame,
- for Re-frame users who already use another React wrapper than Reagent and who want to try Signaali.

Project status: [experimental](https://github.com/metosin/open-source/blob/main/project-status.md#experimental).

## How well does it work?

- The fork is working fine when tested on our own projects and on the port of Re-frame's examples.
- The fork passes all the unit tests in the same way than the original, except for one test in `re-frame.flow.alpha-test`.
  Because this feature is still "alpha", I didn't bother investigate but I suspect that the difference is due to
  the expectation from Re-frame that the Reagent atoms are run eagerly while Signaali's reactive nodes are run lazily.

## Ported Re-frame library

The namespaces were not modified, keep using it as if it was the original Re-frame project.

What's different in the library:
- Reagent was replaced by Siagent.
- Some small changes were needed in `re-frame.interop` to making it work with Signaali.
- `re-frame.interop/after-render` won't work exactly in the same way, but most apps don't use it.

## How to use it in your Re-frame project

In your `deps.edn`, replace the re-frame dependencies with:

```clojure
fi.metosin/si-frame {:mvn/version "1.4.3.0"}
```

This lib should work for you if:
- All the Reagent features your Reagent components (if any) are using are supported by [Siagent](https://github.com/metosin/siagent).
- Your codebase is not directly using the low level implementation of Reagent, e.g. the `reagent.ratom/RAtom` type.

## UIx interop

We provide the extra namespace `re-frame.uix` for UIx users who need to use the subscriptions as React hooks.

Example:
```clojure
(ns my-namespace
  (:require [re-frame.uix :refer [use-subscribe]]
            [uix.core :as uix :refer [$ defui]]))

(defui my-component []
  (let [user-display-name (use-subscribe [:user/display-name])]
    ($ :div "Hello " user-display-name)))
```

## Will this be maintained?

**Yes.**

The change compared to the Re-frame codebase is so small that it will probably be very easy
to make a new release of this fork for any official release of Re-frame after the current `v1.4.3`.

However, I do hope that in the future Re-frame can offer a customization mechanism for its reactivity
such that no project fork is needed.

## Feedback

Your feedback is greatly appreciated and needed. Please let us know how was your experience with our fork
in [![Slack](https://img.shields.io/badge/slack-signaali-orange.svg?logo=slack)](https://clojurians.slack.com/app_redirect?channel=signaali)

---

Below this line is the original content of Re-frame's `README.md`.

---

<p align="center"><a href="https://day8.github.io/re-frame" target="_blank" rel="noopener noreferrer"><img src="docs/images/logo/re-frame-colour.png?raw=true" alt="re-frame logo"></a></p>

## Derived Values, Flowing

> This, milord, is my family's axe. We have owned it for almost nine hundred years, see. Of course,
sometimes it needed a new blade. And sometimes it has required a new handle, new designs on the
metalwork, a little refreshing of the ornamentation ... but is this not the nine hundred-year-old
axe of my family? And because it has changed gently over time, it is still a pretty good axe,
y'know. Pretty good.

> -- Terry Pratchett, The Fifth Elephant <br>
> &nbsp;&nbsp;&nbsp; reflecting on identity, flow and derived values  (aka [The Ship of Theseus](https://en.wikipedia.org/wiki/Ship_of_Theseus))
<br/> 
<br/>

<!--
[![CI](https://github.com/day8/re-frame/workflows/ci/badge.svg)](https://github.com/day8/re-frame/actions?workflow=ci)
[![CD](https://github.com/day8/re-frame/workflows/cd/badge.svg)](https://github.com/day8/re-frame/actions?workflow=cd)
[![License](https://img.shields.io/github/license/day8/re-frame.svg)](license.txt)
-->

## Overview

re-frame is a ClojureScript framework for building user interfaces.
It has a data-oriented, functional design. Its primary focus is on high programmer productivity and scaling up to larger Single-Page applications.

Developed in late 2014, and released in 2015, it is mature and stable. It is used by both small startups and companies with over 500 developers, and it has delivered into production applications which are 40K lines of code and beyond. 

Across the last 6 years, it has outlasted multiple generations of Javascript churn - just imagine your team's productivity if you didn't have to contend with technical churn, and have new magic burn your fingers every two years. Brand new, exciting concepts like recoiljs (in the React world), have been a regular part of re-frame from the beginning. 

re-frame is lucky enough to enjoy an unfair advantage - ClojureScript is a Lisp. Alan Kay
once described Lisp as "Maxwell's equations of software". Paul Graham 
described how Lisp was a competitive advantage for his startup.  When we use Lisp, we 
get to leverage 50 years of foliated excellence from the very best minds available.
And then there's also a thriving ClojureScript community which delivers modern ideas and best-in-class tooling.

Although re-frame leverages React (via Reagent), it only needs 
React to be the V in MVC, and no more. re-frame takes a different road to the currently-pervasive idea that Views should be causal (colocated queries, ComponentDidMount, hooks, etc).
In re-frame, events are causal, and views are purely reactive. 

## Documentation 

The re-frame documentation is [available here](https://day8.github.io/re-frame/).


## The Current Version 

[![Clojars Project](https://img.shields.io/clojars/v/re-frame?labelColor=283C67&color=729AD1&style=for-the-badge&logo=clojure&logoColor=fff)](https://clojars.org/re-frame)

For full dependency information, see the [Clojars page](https://clojars.org/re-frame/)

## Getting Help 

[![Get help on Slack](http://img.shields.io/badge/slack-clojurians%20%23re--frame-97C93C?labelColor=283C67&logo=slack&style=for-the-badge)](https://clojurians.slack.com/channels/re-frame)

## Licence

re-frame is [MIT licenced](license.txt)

