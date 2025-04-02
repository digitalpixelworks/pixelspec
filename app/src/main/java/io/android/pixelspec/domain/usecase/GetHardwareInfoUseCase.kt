package io.android.pixelspec.domain.usecase

import io.android.pixelspec.domain.model.HardwareInfo
import io.android.pixelspec.domain.repository.HardwareRepository
import javax.inject.Inject

class GetHardwareInfoUseCase @Inject constructor(
    private val repository: HardwareRepository
) {
    operator fun invoke(): HardwareInfo = repository.getHardwareInfo()
}