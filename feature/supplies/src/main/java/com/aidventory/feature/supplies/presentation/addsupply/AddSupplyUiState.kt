package com.aidventory.feature.supplies.presentation.addsupply

import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.feature.supplies.R
import java.time.LocalDate


data class AddSupplyUiState(
    val step: Step = Step.Supply(),
    val barcode: String? = null,
    val name: String = "",
    val containers: List<Container> = emptyList(),
    val selectedContainer: Container? = null,
    val supplyUses: List<SupplyUse> = emptyList(),
    val selectedSupplyUses: List<SupplyUse> = emptyList(),
    val expiry: LocalDate? = null,
    val isDone: Boolean = false // for a navigation purpose
) {
    // Do not show the previous button for the first step.
    val isPreviousButtonVisible = step !is Step.Supply

    fun updateToPreviousStep() = copy(step = step.previousStep)
    fun updateToNextStep(): AddSupplyUiState {
        return if (step is Step.Name) {
            // To move from the name step to the next one the name should not be neither empty not blank.
            copy(step = Step.Name(isError = name.isBlank()).nextStep)
        } else {
            copy(step = step.nextStep)
        }
    }

    fun updateSelectedSupplyUses(supplyUse: SupplyUse): AddSupplyUiState {
        val isCurrentlySelected = supplyUse.id in selectedSupplyUses.map { it.id }
        val updatedSelectedSupplyUses = if (isCurrentlySelected) {
            selectedSupplyUses.filterNot { it.id == supplyUse.id }
        } else {
            selectedSupplyUses + supplyUse
        }
        return copy(selectedSupplyUses = updatedSelectedSupplyUses)
    }

    fun updateName(name: String): AddSupplyUiState {
        // Clear the name error when user types.
        return copy(name = name, step = Step.Name(isError = false))
    }

    fun clearSelectedContainer() = copy(selectedContainer = null)

    fun updateSelectedContainer(barcode: String): AddSupplyUiState {
        return copy(
            selectedContainer = containers.find { it.barcode == barcode },
            step = Step.Container(ContainerStepState.Submit)
        )
    }

    sealed interface Step {
        val totalSteps: Int
            get() = values.size
        val ordinal: Int
        val previousStep: Step
        val nextStep: Step

        data class Supply(
            val supplyScannerState: SupplyScannerState = SupplyScannerState.Sense
        ) : Step {
            override val ordinal: Int
                get() = 0
            override val previousStep
                get() = throw IllegalStateException("$this does not have the previous step because it is the initial step.")
            override val nextStep: Step
                get() = Name()
        }

        data class Name(val isError: Boolean = false) : Step {
            override val ordinal: Int
                get() = 1
            override val previousStep: Step
                get() = Supply()
            override val nextStep: Step
                get() = if (isError) this else SupplyUses
        }

        object SupplyUses : Step {
            override val ordinal: Int
                get() = 2
            override val previousStep: Step
                get() = Name()
            override val nextStep: Step
                get() = Expiry
        }

        object Expiry : Step {
            override val ordinal: Int
                get() = 3
            override val previousStep: Step
                get() = SupplyUses
            override val nextStep: Step
                get() = Container()
        }

        data class Container(
            val containerStepState: ContainerStepState = ContainerStepState.Submit
        ) : Step {
            override val ordinal: Int
                get() = 4
            override val previousStep: Step
                get() = Expiry
            override val nextStep: Step
                get() = throw IllegalStateException("$this does not have the next step because it is the last step.")
        }

        companion object {
            val values = listOf(
                Supply(),
                Name(),
                SupplyUses,
                Expiry,
                Container(),
            )
        }
    }

    sealed interface SupplyScannerState {
        object Sense : SupplyScannerState
        object Recognize : SupplyScannerState
        data class AlreadyExistScanResult(val supply: Supply) : SupplyScannerState
    }

    sealed interface ContainerStepState {
        object Submit : ContainerStepState
        object Choose : ContainerStepState
        sealed interface Scan : ContainerStepState {
            object Sense : Scan
            object Recognize : Scan
            object NotFoundScanResult : Scan
        }

    }
}

internal fun AddSupplyUiState.Step.asStringRes(): Int {
    return when (this) {
        is AddSupplyUiState.Step.Supply -> R.string.add_supply_step_scanner
        is AddSupplyUiState.Step.Name -> R.string.add_supply_step_name
        AddSupplyUiState.Step.SupplyUses -> R.string.add_supply_step_supply_uses
        AddSupplyUiState.Step.Expiry -> R.string.add_supply_step_expiry
        is AddSupplyUiState.Step.Container -> R.string.add_supply_step_container
    }
}