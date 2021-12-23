# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [4.4.0 - 2021-12-23]
### Added
- auth-dto 5.2.0

## [4.3.0 - 2021-07-29]
### Added
- added JsonProperty to StatusReportItem

## [4.2.0 - 2021-06-11]
### Added
- Percentage Logger

## [4.1.0 - 2021-03-03]
### Added
- broker constant url for getting site names

## [4.0.1 - 2021-02-25]
### Fixed
- variable names for icinga

## [4.0.0 - 2020-11-06]
- Github release
### Changed
- Samply parent 11.1.0
### Added
- Github Actions
- Google Code Style

## [3.7.0 - 2020-09-10]
### Added
- Add inquiry info status report item parameter

## [3.6.0 - 2020-08-26]
### Added
- Add job config status report item parameter

## [3.5.0 - 2020-08-10]
### Added
- Add query-language constant

## [3.4.0 - 2020-02-12]
### Added
- Add transformer for EssentialSimpleQueryDto
- Activate tests (JUnit 5)

##[3.3.1 - 2019-10-28]
### Changed
- Update share-dto

## [3.3.0 - 2019-08-26]
### Added
- New constants

##[3.2.2 - 2019-06-06]
### Changed
- Modified editForm.xhtml

##[3.2.0 - 2019-06-06]
### Changed
- Update version of samply.common.config 3.0.3 -> 3.1.0 

##[3.1.0 - 2018-06-14]
### Changed
- Update version of mdrfaces 3.0.2 -> 3.1.0 (merged with branch no-jquery)

##[3.0.0 - 2018-05-30]
### Changed
- Java 7 to Java 8
- Update of library versions

##[2.0.5 - 2018-04-26]
### Added
- added parent pom

##[2.0.4 - 2018-04-06]
### Added
- increment version of share-dto

##[2.0.3 - 2018-02-23]
### Fixed
- Fix problem with date format

##[2.0.2 - 2018-02-23]
### Changed
- Update share-dto to 3.1.5

##[2.0.0 - 2017-12-13]
### Changed
- Upgrade to share dto 3.0.0
- Switch to common namespace

## [1.2.1 - 2017-12-01]
### Added
- Autodetect date format and convert it to validation format

## [1.2.0 - 2017-11-27]
### Added
- method to safely combine urls and paths

### Changed
- Change visibility for methods QueryValidator.getExpandedDateValue and QueryValidator.getReformattedDateValue to public
and rename those methods to avoid misunderstandings

## [1.1.12 - 2017-11-08]
### Added
- Optional button for select viewfields action

## [1.1.11 - 2017-11-02]
### Fixed
- Change use of log4j to slf4j in QueryValidator

### Changed
- Upgrade to auth-dto 3.0.0

## [1.1.10 - 2017-10-10]
### Fixed
- Fix crash when "between" operator is present

### Changed
- Throw Parse Exception when trying to convert dates and have the client application handle it

## [1.1.9 - 2017-10-09]

### Changed
- Throw Parse Exception when trying to convert dates and have the client application handle it

## [1.1.8 - 2017-10-09]

### Changed
- Moved functions dealing with query <-> tree conversion to QueryTreeUtil class

### Removed
- Moved QueryConverter functions to share.dto

## [1.1.7 - 2017-09-13]
### Fixed
- Don't try to reformat "is null" or "is not null" nodes

## [1.1.6 - 2017-09-11]
### Added
- Add Method to get a list of mdr dataelements
- Add the option to add a fixed set of items to include in a search

### Changed
- Leave font-weight untouched for form Items. Increases readability
- Remove List style from search results

## [1.1.5 - 2017-08-18]
### Added
- Add Centraxx Mapping Date Parameter to StatusReportItem Class

## [1.1.4 - 2017-08-15]
### Added
- Add Centraxx Mapping Version Parameter to StatusReportItem Class

## [1.1.3 - 2017-08-04]
### Added
- Utils class, collecting utility methods from share client and broker

## [1.1.2 - 2017-08-02]
### Added
- Paths to monitoring added to constants

## [1.1.1 - 2017-07-26]
### Added
- CHANGELOG.md

### Changed
- Only redirect to ADFS logout when "DKFZ" is configured as external identity provider
