package cafe.adriel.voyager.androidx

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi

@InternalVoyagerApi
public inline fun <reified T> Context.findOwner(
    noinline nextFunction: (Context) -> Context? = { (it as? ContextWrapper)?.baseContext }
): T? = generateSequence(seed = this, nextFunction = nextFunction).mapNotNull { context ->
    context as? T
}.firstOrNull()

@InternalVoyagerApi
public val Context.application: Application?
    get() = findOwner<Application> { it.applicationContext }

@InternalVoyagerApi
public val Context.componentActivity: ComponentActivity?
    get() = findOwner<ComponentActivity>()
