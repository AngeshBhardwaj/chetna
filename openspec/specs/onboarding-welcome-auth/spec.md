## Purpose

Chunk 1 of Android onboarding: the Welcome, Contact details, OTP verify, and Signed-in screens that take a guardian from first launch through email-OTP login (ADR-0010) to a temporary signed-in placeholder. TBD — expand as later chunks replace the Signed-in placeholder and add further onboarding steps.

## Requirements

### Requirement: Welcome screen presents trust content and a single forward action
The system SHALL show, on the Welcome screen, the app's value proposition, its trust points (no behavioral profiling, DPDP-aware, emergency calling always works), and a single full-width "Get started" action with no other exit.

#### Scenario: Get started navigates forward
- **WHEN** a guardian taps "Get started" on the Welcome screen
- **THEN** the app navigates to the Contact details screen

### Requirement: Contact method defaults to email, SMS is visibly disabled
The system SHALL present Email as the selected, enabled contact method and Mobile number (SMS) as a visibly disabled "Coming soon" option, per ADR-0010's email-OTP-first decision.

#### Scenario: SMS option cannot be selected
- **WHEN** a guardian taps the "Mobile number (SMS)" option on the Contact details screen
- **THEN** no selection change occurs — Email remains the selected method

### Requirement: Continue is gated on a valid email and explicit consent
The system SHALL keep the Contact details screen's "Continue" action disabled until the entered value is a syntactically valid email address AND the "I agree to receive a verification code at this email address" checkbox is checked.

#### Scenario: Invalid email keeps Continue disabled
- **WHEN** the email field contains a value that fails basic email format validation
- **THEN** "Continue" is disabled, regardless of the consent checkbox state

#### Scenario: Valid email without consent keeps Continue disabled
- **WHEN** the email field contains a valid address but the consent checkbox is unchecked
- **THEN** "Continue" is disabled

#### Scenario: Valid email with consent enables Continue and navigates
- **WHEN** the email field contains a valid address AND the consent checkbox is checked, and the guardian taps "Continue"
- **THEN** the app navigates to the OTP verify screen, carrying the entered email forward

### Requirement: Back navigation preserves previously entered data
The system SHALL provide a back action on the Contact details and OTP verify screens (Welcome has none, being the entry point; Signed-in has none, matching the prototype) that returns to the previous screen in the sequence without discarding data already entered on it.

#### Scenario: Back from Contact details returns to Welcome
- **WHEN** a guardian taps back on the Contact details screen
- **THEN** the app navigates to the Welcome screen

#### Scenario: Back from OTP verify returns to Contact details with the email retained
- **WHEN** a guardian taps back on the OTP verify screen after having entered an email on Contact details
- **THEN** the app navigates to the Contact details screen with the previously entered email still populated in the field

#### Scenario: Back is the recovery path from a locked OTP screen
- **WHEN** the OTP verify screen is in its locked state (3 wrong attempts)
- **THEN** the back action remains available and is the only way to leave the screen and request a new code — there is no separate in-body "request new code" action

### Requirement: Resend requests a new code without resetting attempts
The system SHALL disable the "Resend code" action for a fixed countdown period after a code is (re)sent, re-enabling it once the countdown reaches zero. Using Resend SHALL NOT reset the wrong-attempt counter from the "Wrong OTP code allows up to 3 attempts" requirement.

#### Scenario: Resend disabled during countdown
- **WHEN** the OTP verify screen has been shown for less than the countdown duration since the code was last (re)sent
- **THEN** the "Resend code" action is disabled and displays the remaining countdown time

#### Scenario: Resend does not reset attempts
- **WHEN** a guardian has 1 wrong attempt remaining and then successfully uses Resend once the countdown allows it
- **THEN** the attempt counter remains at 1 remaining attempt, not reset back to 3

### Requirement: OTP entry auto-advances and auto-verifies
The system SHALL present 6 individual digit fields that auto-advance focus to the next field on entry, and automatically check the entered code once all 6 digits are filled, without requiring an explicit "Verify" action.

#### Scenario: Digit entry advances focus
- **WHEN** a guardian enters a digit in OTP field N (N < 6)
- **THEN** input focus moves to field N+1

#### Scenario: Correct code verifies and navigates
- **WHEN** all 6 digits are entered and match the expected mock code
- **THEN** the screen shows a "Verified" state and the app navigates to the Signed-in screen shortly after

### Requirement: Wrong OTP code allows up to 3 attempts before locking
The system SHALL allow a guardian up to 3 total attempts at entering the correct code; each wrong attempt shows an error state and a "Clear and try again" action that resets the fields for another attempt. After the 3rd wrong attempt, the screen locks and directs the guardian to request a new code.

#### Scenario: First wrong attempt allows retry
- **WHEN** the entered 6-digit code does not match the expected code, on the 1st or 2nd attempt
- **THEN** the fields show an error state, remaining-attempts count is displayed, and "Clear and try again" is available

#### Scenario: Third wrong attempt locks the screen
- **WHEN** the entered 6-digit code does not match the expected code on the 3rd attempt
- **THEN** the OTP fields become disabled, "Clear and try again" is no longer offered, and a message directs the guardian to request a new code

### Requirement: Signed-in screen is an explicit, temporary scaffold
The system SHALL show, on the Signed-in screen, the verified email and a Logout action, along with a visible note that this screen is a temporary placeholder to be replaced by later chunks.

#### Scenario: Logout returns to Welcome and clears state
- **WHEN** a guardian taps "Logout" on the Signed-in screen
- **THEN** the app navigates back to the Welcome screen and any previously entered email/OTP state is cleared, so a fresh login attempt starts from empty fields
</content>
