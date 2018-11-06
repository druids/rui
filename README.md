RUI - Reactive UI components
============================

RUI contains simple reusable UI components based on Reagent and Re-frame. For more information about components see
[wiki](https://github.com/druids/rui/wiki). All components use Bootstrap classes, but Bootstrap itself is **NOT**
included (it's up to a host project). Please keep on mind that Re-frame nad Reagent are **NOT** included.

[![CircleCI](https://circleci.com/gh/druids/rui.svg?style=svg)](https://circleci.com/gh/druids/rui)
[![Dependencies Status](https://jarkeeper.com/druids/rui/status.png)](https://jarkeeper.com/druids/rui)
[![License](https://img.shields.io/badge/MIT-Clause-blue.svg)](https://opensource.org/licenses/MIT)


Leiningen/Boot
--------------

```clojure
[druids/rui "0.10.0"]
```

Documentation
-------------

* [`rui.alerts` - Bootstrap's alerts](https://github.com/druids/rui/blob/master/src/cljs/rui/alerts/components.cljs)
* [`rui.buttons` - Buttons components](https://github.com/druids/rui/blob/master/src/cljs/rui/buttons/components.cljs)
* [`rui.flash` - Flash components for messages](https://github.com/druids/rui/blob/master/src/cljs/rui/flash/)
* [`rui.icons` - Icons components based on Font Awesome](https://github.com/druids/rui/blob/master/src/cljs/rui/icons/)
* [`rui.forms` - Forms UI components and function for handling their states](https://github.com/druids/rui/blob/master/src/cljs/rui/forms/)
* [`rui.modals` - Bootstrap's modal components and function for handling their states](https://github.com/druids/rui/blob/master/src/cljs/rui/modals/)
