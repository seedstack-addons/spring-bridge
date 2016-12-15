# Version 3.0.0 (2016-12-15)

* [brk] Update to SeedStack 16.11 new configuration system.
* [brk] JPA unit property used to specify an explicit unit name has been renamed from `org.seedstack.jpa.unit-name` to `seedstack.jpaUnitName`.

# Version 2.2.1 (2016-04-26)

* [chg] Update for SeedStack 16.4.
* [fix] Correctly cleanup `ThreadLocal` in `SpringTransactionStatusLink`.

# Version 2.2.0 (2015-11-25)

No change.

# Version 2.1.0 (2015-11-17)

* [new] `@WithApplicationContexts` is now supported on test methods in conjunction with `@WithCommandLine` enabling a per-method alteration of the test Spring context.
* [chg] Refactored as an add-on and updated to work with Seed 2.1.0+

# Version 2.0.0 (2015-07-30)

* [new] Initial Open-Source release.
