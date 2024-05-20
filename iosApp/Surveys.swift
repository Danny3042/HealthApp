//
//  Surveys.swift
//  iosApp
//
//  Created by Daniel Ramzani on 19/05/2024.
//  Copyright © 2024 orgName. All rights reserved.
//



import CareKitStore
import ResearchKit

struct Surveys {

    private init() {}

    // MARK: Onboarding

    static func onboardingSurvey() -> ORKTask {
        
        // The Welcome Instruction step.
        let welcomeInstructionStep = ORKInstructionStep(
            identifier: "onboarding.welcome"
        )

        welcomeInstructionStep.title = "Welcome!"
        welcomeInstructionStep.detailText = "Thank you for joining our study. Tap Next to learn more before signing up."
        welcomeInstructionStep.image = UIImage(named: "welcome-image")
        welcomeInstructionStep.imageContentMode = .scaleAspectFill
        
        // The Informed Consent Instruction step.
        let studyOverviewInstructionStep = ORKInstructionStep(
            identifier: "onboarding.overview"
        )

        studyOverviewInstructionStep.title = "Before You Join"
        studyOverviewInstructionStep.iconImage = UIImage(systemName: "checkmark.seal.fill")
        
        let heartBodyItem = ORKBodyItem(
            text: "The study will ask you to share some of your health data.",
            detailText: nil,
            image: UIImage(systemName: "heart.fill"),
            learnMoreItem: nil,
            bodyItemStyle: .image
        )

        let completeTasksBodyItem = ORKBodyItem(
            text: "You will be asked to complete various tasks over the duration of the study.",
            detailText: nil,
            image: UIImage(systemName: "checkmark.circle.fill"),
            learnMoreItem: nil,
            bodyItemStyle: .image
        )

        let signatureBodyItem = ORKBodyItem(
            text: "Before joining, we will ask you to sign an informed consent document.",
            detailText: nil,
            image: UIImage(systemName: "signature"),
            learnMoreItem: nil,
            bodyItemStyle: .image
        )

        let secureDataBodyItem = ORKBodyItem(
            text: "Your data is kept private and secure.",
            detailText: nil,
            image: UIImage(systemName: "lock.fill"),
            learnMoreItem: nil,
            bodyItemStyle: .image
        )
        
        studyOverviewInstructionStep.bodyItems = [
            heartBodyItem,
            completeTasksBodyItem,
            signatureBodyItem,
            secureDataBodyItem
        ]

        // The Signature step (using WebView).
        let webViewStep = ORKWebViewStep(
            identifier: "onboarding.signatureCapture",
            html: informedConsentHTML
        )

        webViewStep.showSignatureAfterContent = true
        
        // The Request Permissions step.
        let healthKitTypesToWrite: Set<HKSampleType> = [
            HKObjectType.quantityType(forIdentifier: .bodyMassIndex)!,
            HKObjectType.quantityType(forIdentifier: .activeEnergyBurned)!,
            HKObjectType.workoutType()
        ]

        let healthKitTypesToRead: Set<HKObjectType> = [
            HKObjectType.characteristicType(forIdentifier: .dateOfBirth)!,
            HKObjectType.workoutType(),
            HKObjectType.quantityType(forIdentifier: .appleStandTime)!,
            HKObjectType.quantityType(forIdentifier: .appleExerciseTime)!
        ]

        let healthKitPermissionType = ORKHealthKitPermissionType(
            sampleTypesToWrite: healthKitTypesToWrite,
            objectTypesToRead: healthKitTypesToRead
        )

        let notificationsPermissionType = ORKNotificationPermissionType(
            authorizationOptions: [.alert, .badge, .sound]
        )

        let motionPermissionType = ORKMotionActivityPermissionType()

        let requestPermissionsStep = ORKRequestPermissionsStep(
            identifier: "onboarding.requestPermissionsStep",
            permissionTypes: [
                healthKitPermissionType,
                notificationsPermissionType,
                motionPermissionType
            ]
        )

        requestPermissionsStep.title = "Health Data Request"
        requestPermissionsStep.text = "Please review the health data types below and enable sharing to contribute to the study."

        // Completion Step
        let completionStep = ORKCompletionStep(
            identifier: "onboarding.completionStep"
        )

        completionStep.title = "Enrollment Complete"
        completionStep.text = "Thank you for enrolling in this study. Your participation will contribute to meaningful research!"

        let surveyTask = ORKOrderedTask(
            identifier: "onboard",
            steps: [
                welcomeInstructionStep,
                studyOverviewInstructionStep,
                webViewStep,
                requestPermissionsStep,
                completionStep
            ]
        )

        return surveyTask
    }

    // MARK: Check-in Survey

    static let checkInIdentifier = "checkin"
    static let checkInFormIdentifier = "checkin.form"
    static let checkInPainItemIdentifier = "checkin.form.pain"
    static let checkInSleepItemIdentifier = "checkin.form.sleep"

    static func checkInSurvey() -> ORKTask {

        let painAnswerFormat = ORKAnswerFormat.scale(
            withMaximumValue: 10,
            minimumValue: 1,
            defaultValue: 0,
            step: 1,
            vertical: false,
            maximumValueDescription: "Very painful",
            minimumValueDescription: "No pain"
        )

        let sleepAnswerFormat = ORKAnswerFormat.scale(
            withMaximumValue: 12,
            minimumValue: 0,
            defaultValue: 0,
            step: 1,
            vertical: false,
            maximumValueDescription: nil,
            minimumValueDescription: nil
        )

        let painItem = ORKFormItem(
            identifier: checkInPainItemIdentifier,
            text: "How would you rate your pain?",
            answerFormat: painAnswerFormat
        )
        painItem.isOptional = false

        let sleepItem = ORKFormItem(
            identifier: checkInSleepItemIdentifier,
            text: "How many hours of sleep did you get last night?",
            answerFormat: sleepAnswerFormat
        )
        sleepItem.isOptional = false

        let formStep = ORKFormStep(
            identifier: checkInFormIdentifier,
            title: "Check In",
            text: "Please answer the following questions."
        )
        formStep.formItems = [painItem, sleepItem]
        formStep.isOptional = false

        let surveyTask = ORKOrderedTask(
            identifier: checkInIdentifier,
            steps: [formStep]
        )

        return surveyTask
    }

    static func extractAnswersFromCheckInSurvey(
        _ result: ORKTaskResult) -> [OCKOutcomeValue]? {

        guard
            let response = result.results?
                .compactMap({ $0 as? ORKStepResult })
                .first(where: { $0.identifier == checkInFormIdentifier }),

            let scaleResults = response
                .results?.compactMap({ $0 as? ORKScaleQuestionResult }),

            let painAnswer = scaleResults
                .first(where: { $0.identifier == checkInPainItemIdentifier })?
                .scaleAnswer,

            let sleepAnswer = scaleResults
                .first(where: { $0.identifier == checkInSleepItemIdentifier })?
                .scaleAnswer
        else {
            assertionFailure("Failed to extract answers from check in survey!")
            return nil
        }

        var painValue = OCKOutcomeValue(Double(truncating: painAnswer))
        painValue.kind = checkInPainItemIdentifier

        var sleepValue = OCKOutcomeValue(Double(truncating: sleepAnswer))
        sleepValue.kind = checkInSleepItemIdentifier

        return [painValue, sleepValue]
    }

    // MARK: Range of Motion.

    static func rangeOfMotionCheck() -> ORKTask {

        let rangeOfMotionOrderedTask = ORKOrderedTask.kneeRangeOfMotionTask(
            withIdentifier: "rangeOfMotionTask",
            limbOption: .left,
            intendedUseDescription: nil,
            options: [.excludeConclusion]
        )

        let completionStep = ORKCompletionStep(identifier: "rom.completion")
        completionStep.title = "All done!"
        completionStep.detailText = "We know the road to recovery can be tough. Keep up the good work!"

        rangeOfMotionOrderedTask.appendSteps([completionStep])
        
        return rangeOfMotionOrderedTask
    }

    static func extractRangeOfMotionOutcome(
        _ result: ORKTaskResult) -> [OCKOutcomeValue]? {

        guard let motionResult = result.results?
            .compactMap({ $0 as? ORKStepResult })
            .compactMap({ $0.results })
            .flatMap({ $0 })
            .compactMap({ $0 as? ORKRangeOfMotionResult })
            .first else {

            assertionFailure("Failed to parse range of motion result")
            return nil
        }

        var range = OCKOutcomeValue(motionResult.range)
        range.kind = #keyPath(ORKRangeOfMotionResult.range)

        return [range]
    }

    // MARK: 3D Knee Model
    
    static func kneeModel() -> ORKTask {

        let instructionStep = ORKInstructionStep(
            identifier: "insights.instructionStep"
        )
        instructionStep.title = "Your Injury Visualized"
        instructionStep.detailText = "A 3D model will be presented to give you better insights on your specific injury."
        instructionStep.iconImage = UIImage(systemName: "bandage")

        let modelManager = ORKUSDZModelManager(usdzFileName: "toy_robot_vintage")

        let kneeModelStep = ORK3DModelStep(
            identifier: "insights.kneeModel",
            modelManager: modelManager
        )

        let kneeModelTask = ORKOrderedTask(
            identifier: "insights",
            steps: [instructionStep, kneeModelStep]
        )

        return kneeModelTask
    }
}
