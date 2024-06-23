//
//  HealthKitManager.swift
//  iosApp
//
//  Created by Daniel Ramzani on 23/06/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import HealthKit

class HealthKitManager {
    private let healthStore = HKHealthStore()

    func checkAuthorization(completion: @escaping (Bool) -> Void) {
        let readDataTypes: Set<HKObjectType> = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
            HKObjectType.quantityType(forIdentifier: .activeEnergyBurned)!,
            HKObjectType.categoryType(forIdentifier: .sleepAnalysis)!
        ]
        healthStore.requestAuthorization(toShare: [], read: readDataTypes) { (success, error) in
            completion(success)
        }
    }

    func getSteps(completion: @escaping (String, Error?) -> Void) {
        fetchData(for: .stepCount, unit: HKUnit.count(), completion: completion)
    }

    func getActiveMinutes(completion: @escaping (String, Error?) -> Void) {
        fetchData(for: .activeEnergyBurned, unit: HKUnit.kilocalorie(), completion: completion)
    }

    func getDistance(completion: @escaping (String, Error?) -> Void) {
        fetchData(for: .distanceWalkingRunning, unit: HKUnit.meter(), completion: completion)
    }

    func getSleepDuration(completion: @escaping (String, Error?) -> Void) {
        guard let dataType = HKObjectType.categoryType(forIdentifier: .sleepAnalysis) else {
            completion("0", nil)
            return
        }

        let now = Date()
        let startOfDay = Calendar.current.startOfDay(for: now)
        let predicate = HKQuery.predicateForSamples(withStart: startOfDay, end: now, options: .strictStartDate)

        let query = HKSampleQuery(sampleType: dataType, predicate: predicate, limit: HKObjectQueryNoLimit, sortDescriptors: nil) { _, results, error in
            guard error == nil else {
                completion("0", error)
                return
            }

            let total = results?
                .compactMap { $0 as? HKCategorySample }
                .reduce(0, { $0 + $1.value })

            completion("\(total ?? 0)", nil)
        }

        healthStore.execute(query)
    }

    private func fetchData(for identifier: HKQuantityTypeIdentifier, unit: HKUnit, completion: @escaping (String, Error?) -> Void) {
        guard let dataType = HKObjectType.quantityType(forIdentifier: identifier) else {
            completion("0", nil)
            return
        }

        let now = Date()
        let startOfDay = Calendar.current.startOfDay(for: now)
        let predicate = HKQuery.predicateForSamples(withStart: startOfDay, end: now, options: .strictStartDate)

        let query = HKSampleQuery(sampleType: dataType, predicate: predicate, limit: HKObjectQueryNoLimit, sortDescriptors: nil) { _, results, error in
            guard error == nil else {
                completion("0", error)
                return
            }

            let total = results?
                .compactMap { $0 as? HKQuantitySample }
                .reduce(0, { $0 + $1.quantity.doubleValue(for: unit) })

            completion("\(total ?? 0)", nil)
        }

        healthStore.execute(query)
    }
}
