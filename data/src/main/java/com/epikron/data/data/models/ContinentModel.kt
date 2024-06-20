package com.epikron.data.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.epikron.countriesandflags.ContinentsQuery

@Entity
public data class ContinentModel(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "continent_code") val code: String,
    @ColumnInfo(name = "continent_name") val name: String
) {
    public companion object {
        public val list: List<ContinentModel> = listOf(
            ContinentModel(0,"AN","Antarctica"),
            ContinentModel(1,"AF","Africa"),
            ContinentModel(2,"AS","Asia"),
            ContinentModel(3,"EU", "Europe"),
            ContinentModel(4,"NA","North America"),
            ContinentModel(5,"OC","Oceania"),
            ContinentModel(6,"SA","South America")
        )
        public val empty: ContinentModel = ContinentModel(0, "", "")
        public val test: ContinentModel = ContinentModel(0, "eu", "Europe")

        public fun ContinentsQuery.Continent.toContinentModel(): ContinentModel {
            return ContinentModel(
                code = this.code ?: "",
                name = this.name ?: ""
            )
        }
    }
}
