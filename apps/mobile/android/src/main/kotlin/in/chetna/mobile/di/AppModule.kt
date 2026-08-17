package `in`.chetna.mobile.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Empty scaffold — per-screen Hilt bindings are added as each screen's
// ViewModel needs a dependency (Sections 5-7), not speculatively here.
@Module
@InstallIn(SingletonComponent::class)
object AppModule
