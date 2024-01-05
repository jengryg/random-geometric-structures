package spaces

/**
 * [DoubleArray] + [DoubleArray] component wise calculated, i.e. vector addition.
 */
operator fun DoubleArray.plus(other: DoubleArray): DoubleArray {
    return DoubleArray(this.size) {
        this[it] + other[it]
    }
}

/**
 * [DoubleArray] - [DoubleArray] component wise calculated, i.e. vector subtractions.
 */
operator fun DoubleArray.minus(other: DoubleArray): DoubleArray {
    return DoubleArray(this.size) {
        this[it] - other[it]
    }
}

/**
 * [DoubleArray] + [IntArray] component wise calculated, i.e. vector addition.
 */
operator fun DoubleArray.plus(other: IntArray): DoubleArray {
    return DoubleArray(this.size) {
        this[it] + other[it]
    }
}

/**
 * [DoubleArray] - [IntArray] component wise calculated, i.e. vector subtractions.
 */
operator fun DoubleArray.minus(other: IntArray): DoubleArray {
    return DoubleArray(this.size) {
        this[it] - other[it]
    }
}