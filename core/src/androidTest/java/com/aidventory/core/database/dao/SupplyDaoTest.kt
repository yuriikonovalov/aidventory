package com.aidventory.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aidventory.core.database.AidventoryDatabase
import com.aidventory.core.database.model.ContainerEntity
import com.aidventory.core.database.model.SupplyEntity
import com.aidventory.core.database.model.SupplySupplyUseCrossRef
import com.aidventory.core.database.model.SupplyUseEntity
import com.google.common.truth.Truth.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class SupplyDaoTest {
    private lateinit var db: AidventoryDatabase
    private lateinit var supplyDao: SupplyDao
    private lateinit var supplyUseDao: SupplyUseDao
    private lateinit var containerDao: ContainerDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AidventoryDatabase::class.java).build()
        containerDao = db.containerDao()
        supplyDao = db.supplyDao()
        supplyUseDao = db.supplyUseDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    /* TODO: replace runBlocking{} with runTest{}
     * by solving: java.lang.NoClassDefFoundError: Failed resolution of: Lkotlinx/coroutines/DelayWithTimeoutDiagnostics;
     */
    @Test
    fun getSupplies_whenUsedContainerBarcodeFilter_shouldFetchSuppliesWithContainerBarcodes(): Unit =
        runBlocking {
            val containerEntities = listOf(
                ContainerEntity(barcode = "1", "Container#1", LocalDate.now()),
                ContainerEntity(barcode = "2", "Container#2", LocalDate.now())
            )
            val expectedContainerEntity = containerEntities[0]
            val expectedContainerBarcodes = setOf(expectedContainerEntity.barcode)
            val supplyEntities = listOf(
                SupplyEntity(
                    barcode = "02",
                    name = "Supply#02",
                    expiry = LocalDate.now(),
                    containerBarcode = expectedContainerEntity.barcode,
                    isBarcodeGenerated = true
                ),
                SupplyEntity(
                    barcode = "01",
                    name = "Supply#01",
                    expiry = LocalDate.now(),
                    containerBarcode = expectedContainerEntity.barcode,
                    isBarcodeGenerated = true
                ),

                SupplyEntity(
                    barcode = "03",
                    name = "Supply#03",
                    expiry = LocalDate.now(),
                    containerBarcode = containerEntities[1].barcode,
                    isBarcodeGenerated = true
                ),
                SupplyEntity(
                    barcode = "04",
                    name = "Supply#04",
                    expiry = LocalDate.now(),
                    containerBarcode = containerEntities[1].barcode,
                    isBarcodeGenerated = true
                )
            )

            // Insert containers.
            containerEntities.forEach { containerDao.upsert(it) }
            // Insert supplies.
            supplyEntities.forEach { supplyDao.upsert(it) }


            val actualSupplyEntities = supplyDao.getSupplies(
                useFilterContainerBarcodes = true,
                filterContainerBarcodes = expectedContainerBarcodes
            )
                .first()
                .map { it.supplyEntity }

            val actualContainerBarcodes = actualSupplyEntities
                .map { it.containerBarcode }
                .toSet()

            assertThat(actualSupplyEntities).hasSize(2)
            assertThat(actualContainerBarcodes).isEqualTo(expectedContainerBarcodes)
        }

    @Test
    fun getSupplies_whenUsedSupplyUseFilter_shouldFetchSuppliesWithSupplyUses(): Unit =
        runBlocking {
            val containerEntities = listOf(
                ContainerEntity(barcode = "1", "Container#1", LocalDate.now()),
                ContainerEntity(barcode = "2", "Container#2", LocalDate.now())
            )
            val supplyUseEntities = listOf(
                SupplyUseEntity(id = 1, name = "SupplyUse#1", isDefault = true),
                SupplyUseEntity(id = 2, name = "SupplyUse#2", isDefault = true)
            )

            val filterSupplyUseId = supplyUseEntities[0].id

            val supplyEntities = listOf(
                SupplyEntity(
                    barcode = "02",
                    name = "Supply#02",
                    expiry = LocalDate.now(),
                    containerBarcode = containerEntities[1].barcode,
                    isBarcodeGenerated = true
                ),
                SupplyEntity(
                    barcode = "01",
                    name = "Supply#01",
                    expiry = LocalDate.now(),
                    containerBarcode = containerEntities[1].barcode,
                    isBarcodeGenerated = true
                ),

                SupplyEntity(
                    barcode = "03",
                    name = "Supply#03",
                    expiry = LocalDate.now(),
                    containerBarcode = containerEntities[1].barcode,
                    isBarcodeGenerated = true
                ),
                SupplyEntity(
                    barcode = "04",
                    name = "Supply#04",
                    expiry = LocalDate.now(),
                    containerBarcode = containerEntities[1].barcode,
                    isBarcodeGenerated = true
                )
            )


            val supplySupplyUseCrossRefEntities = listOf(
                SupplySupplyUseCrossRef(
                    supplyId = supplyEntities[0].barcode,
                    supplyUseId = filterSupplyUseId
                ),
                SupplySupplyUseCrossRef(
                    supplyId = supplyEntities[1].barcode,
                    supplyUseId = filterSupplyUseId
                ),
                SupplySupplyUseCrossRef(
                    supplyId = supplyEntities[2].barcode,
                    supplyUseId = supplyUseEntities[1].id
                )
            )

            val expectedSupplyEntities = listOf(supplyEntities[0], supplyEntities[1])

            // Insert container entities.
            containerEntities.forEach { containerDao.upsert(it) }
            // Insert supply entities.
            supplyEntities.forEach { supplyDao.upsert(it) }
            // Insert supply use entities.
            supplyUseEntities.forEach { supplyUseDao.insert(it) }
            // Insert cross refs.
            supplyDao.insertSupplySupplyUseCrossRefEntities(supplySupplyUseCrossRefEntities)


            val actualSupplyEntities = supplyDao.getSupplies(
                useFilterSupplyUseIds = true,
                filterSupplyUseIds = setOf(filterSupplyUseId)
            )
                .first()
                .map { it.supplyEntity }


            assertThat(actualSupplyEntities).hasSize(2)
            assertThat(actualSupplyEntities).isEqualTo(expectedSupplyEntities)
        }

    @Test
    fun getCountSupplyWithContainer_whenSupplyHasContainer_shouldReturn1(): Unit = runBlocking {
        val containerEntity = ContainerEntity(barcode = "1", "Container#1", LocalDate.now())
        val expectedSupplyEntity = SupplyEntity(
            barcode = "02",
            name = "Supply#02",
            expiry = LocalDate.now(),
            containerBarcode = containerEntity.barcode,
            isBarcodeGenerated = true
        )

        val supplyEntities = listOf(
            SupplyEntity(
                barcode = "01",
                name = "Supply#01",
                expiry = LocalDate.now(),
                containerBarcode = null,
                isBarcodeGenerated = true
            ),
            expectedSupplyEntity
        )

        // Insert container.
        containerDao.upsert(containerEntity)
        // Insert supplies.
        supplyEntities.forEach { supplyDao.upsert(it) }

        val actualCount = supplyDao.getCountSupplyWithContainer(
            supplyBarcode = expectedSupplyEntity.barcode,
            containerBarcode = expectedSupplyEntity.containerBarcode!!
        )

        assertThat(actualCount).isEqualTo(1)
    }

    @Test
    fun getCountSupplyWithContainer_whenSupplyHasNoContainer_shouldReturn0(): Unit = runBlocking {
        val containerEntities = listOf(
            ContainerEntity(barcode = "1", "Container#1", LocalDate.now()),
            ContainerEntity(barcode = "2", "Container#2", LocalDate.now())
        )

        val supplyEntities = listOf(
            SupplyEntity(
                barcode = "01",
                name = "Supply#01",
                expiry = LocalDate.now(),
                containerBarcode = null,
                isBarcodeGenerated = true
            ),
            SupplyEntity(
                barcode = "02",
                name = "Supply#02",
                expiry = LocalDate.now(),
                containerBarcode = containerEntities[0].barcode,
                isBarcodeGenerated = true
            ),
        )

        // Insert containers.
        containerEntities.forEach { containerDao.upsert(it) }
        // Insert supplies.
        supplyEntities.forEach { supplyDao.upsert(it) }

        val actualCount = supplyDao.getCountSupplyWithContainer(
            supplyBarcode = supplyEntities[1].barcode,
            containerBarcode = containerEntities[1].barcode
        )

        assertThat(actualCount).isEqualTo(0)
    }

    @Test
    fun getExpiredSupplies_shouldReturnSuppliesWithExpiryBeforeTodayInclusive(): Unit =
        runBlocking {
            val supplyEntities = listOf(
                SupplyEntity("1", true, "1", null, null),
                SupplyEntity("2", true, "2", LocalDate.now().minusDays(6), null),
                SupplyEntity("3", true, "3", LocalDate.now().plusMonths(1), null),
                SupplyEntity("4", true, "4", LocalDate.now(), null),
            )
            val expectedSupplyEntities = listOf(supplyEntities[1], supplyEntities[3])

            supplyDao.insert(supplyEntities)
            val actualSupplyEntities = supplyDao.getExpiredSupplies().first()
                .map { it.supplyEntity }

            assertThat(actualSupplyEntities).containsExactlyElementsIn(expectedSupplyEntities)
        }

    @Test
    fun getExpiredTodaySupplies_shouldReturnSuppliesWithExpiryToday(): Unit = runBlocking {
        val supplyEntities = listOf(
            SupplyEntity("1", true, "1", null, null),
            SupplyEntity("2", true, "2", LocalDate.now().minusDays(6), null),
            SupplyEntity("3", true, "3", LocalDate.now().plusMonths(1), null),
            SupplyEntity("4", true, "4", LocalDate.now(), null),
        )
        val expectedSupplyEntities = listOf(supplyEntities[3])

        supplyDao.insert(supplyEntities)
        val actualSupplyEntities = supplyDao.getExpiredTodaySupplies()
            .map { it.supplyEntity }

        assertThat(actualSupplyEntities).containsExactlyElementsIn(expectedSupplyEntities)
    }
}